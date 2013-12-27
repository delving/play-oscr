package storage

import eu.delving.basex.client._
import org.basex.server.ClientSession
import play.api.mvc.{Result, Controller}
import java.util.Date
import java.text.SimpleDateFormat

class BaseXController extends Controller with BaseXBridge {

  def findOneResult(query: String, session: ClientSession): Result = {
    session.findOne(query) match {
      case Some(xml) => Ok(xml)
      case None => NotFound
    }
  }

}

object BaseXConnection {
	lazy val server = new BaseX(host = "localhost", port = 1984, eport = 2013, user = "admin", pass = "admin")

	def withSession[T](block: ClientSession => T) = {
		server.withSession("oscr") {
			session =>
				block(session)
		}
	}
}

trait BaseXBridge {

	val database = "oscr"

	def langDocument(lang: String) = s"/i18n/$lang.xml"

	def langPath(lang: String): String = s"doc('$database${langDocument(lang)}')/Language"

	def groupCollection = s"collection('$database/people/groups')/Group"

	def docCollection(schemaName: String) = s"collection('$database/documents/$schemaName')"

	def userCollection = s"collection('$database/people/users')/User"

	def userDocument(identifier: String) = s"/people/users/$identifier.xml"

	def userPath(identifier: String) = s"doc('$database${userDocument(identifier)}')/User"

	def groupDocument(identifier: String) = s"/people/groups/$identifier.xml"

	def groupPath(identifier: String) = s"doc('$database${groupDocument(identifier)}')/Group"

  def vocabDocument(vocabName: String) = s"/vocabulary/$vocabName.xml"

  def vocabPath(vocabName:String) = s"doc('$database${vocabDocument(vocabName)}')"

  def vocabExists(vocabName: String) = s"db:exists('$database','${vocabDocument(vocabName)}')"

  def vocabAdd(vocabName: String, xml:String) = s"db:add('$database', $xml,'${vocabDocument(vocabName)}')"

  def logDocument = s"/log/${new SimpleDateFormat("yyyy-MM-dd").format(new Date())}.xml"

  def logPath= s"doc('$database$logDocument')"

	def schemaPath = s"doc('$database/Schemas.xml')/Schemas"

  def docDocument(schemaName: String, identifier: String) = s"/documents/$schemaName/$identifier.xml"

  def docPath(schemaName:String, identifier:String) = s"doc('$database${docDocument(schemaName, identifier)}')"

	def inXml(value: String) = {
		value.replace("<", "&lt;").replace(">", "&gt;")
	}

	def quote(value: String) = {
		value match {
			case "" => "''"
			case string =>
				"'" + string.replace("'", "\'\'") + "'"
		}
	}

	def execute(command: String, session: ClientSession) = session.execute(
		s"<xquery><![CDATA[$command]]></xquery>"
	)

  def generateId(prefix: String) = {
    val millisSince2013 = (new Date().getTime - new Date(2013, 1, 1).getTime).toLong
    val randomNumber = Math.floor(Math.random() * 36 * 36 * 36).toLong
    val randomString: String = java.lang.Long.toString(randomNumber, 36)
    val padded: String = randomString.reverse.padTo(3, "0").toString().reverse
    s"OSCR-$prefix-${java.lang.Long.toString(millisSince2013, 36)}-$padded"
  }
}
