package storage

import eu.delving.basex.client.BaseX
import org.basex.server.ClientSession

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

	def langDocument(lang: String) = {
		s"/i18n/$lang.xml"
	}

	def langPath(lang: String): String = s"doc('$database${langDocument(lang)}')/Language"

	def groupCollection = s"collection('$database/people/groups')/Group"

	def docCollection(schemaName: String) = s"collection('$database/documents/$schemaName')"

	def userCollection = s"collection('$database/people/users')/User"

	def userDocument(identifier: String) = s"/people/users/$identifier.xml"

	def userPath(identifier: String) = s"doc('$database${userDocument(identifier)}')/User"

  //  this.vocabDocument = function (vocabName) {
  //    return "/vocabulary/" + vocabName + ".xml";
  //  };
  //
  //  this.vocabPath = function (vocabName) {
  //    return "doc('" + this.database + this.vocabDocument(vocabName) + "')";
  //  };
  //
  //  this.vocabExists = function (vocabName) {
  //    return "db:exists('" + this.database + "','" + this.vocabDocument(vocabName) + "')";
  //  };
  //
  //  this.vocabAdd = function (vocabName, xml) {
  //    return "db:add('" + this.database + "', " + xml + ",'" + this.vocabDocument(vocabName) + "')";
  //  };
  //
  //  this.docDocument = function (schemaName, identifier) {
  //    if (!schemaName) throw new Error("No schema name!");
  //    if (!identifier) throw new Error("No identifier!");
  //    return "/documents/" + schemaName + "/" + identifier + ".xml";
  //  };
  //
  //  this.docPath = function (schemaName, identifier) {
  //    return "doc('" + this.database + this.docDocument(schemaName, identifier) + "')/Document";
  //  };
  //
  //  this.logDocument = function () {
  //    var now = new Date();
  //    return "/log/" + now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() + ".xml";
  //  };
  //
  //  this.logPath = function () {
  //    return "doc('" + this.database + this.logDocument() + "')";
  //  };

  def schemaPath = s"doc('$database/Schemas.xml')/Schemas"

  def groupDocument(identifier: String) = s"/people/groups/$identifier.xml"

  def groupPath(identifier: String) = s"doc('$database${groupDocument(identifier)}')/Group"

	def groupCollection(identifier: String) = s"collection('$database/people/groups')/Group"

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


}
