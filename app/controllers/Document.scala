package controllers

import play.api.mvc._
import eu.delving.basex.client._
import services.{BaseXController, BaseXConnection}
import play.Logger
import play.api.libs.json.JsValue
import scala.compat.Platform
import scala.xml.transform.{RuleTransformer, RewriteRule}
import scala.xml.{Attribute, Text, Elem, Node}
import scala.concurrent.Future
import java.io.ByteArrayInputStream

object Document extends BaseXController {

  val MAX_RESULTS = 30

  def getDocumentSchema(schemaName: String) = Action(
    BaseXConnection.withSession(findOneResult(s"$schemaPath/Document/$schemaName", _))
  )

  def getDocument(schemaName: String, identifier: String) = Action(
    BaseXConnection.withSession(findOneResult(docPath(schemaName, identifier), _))
  )

  def listDocuments(schemaName: String) = Action(
    BaseXConnection.withSession(
      session => {
        val query =
          s"let $$all := for $$doc in ${docCollection(schemaName)}" +
          s" order by $$doc/Header/Timestamp descending return $$doc" +
          s" return subsequence($$all, 1, $MAX_RESULTS)"
        val xmlList = session.find(query).toList
        Ok(<Documents>{for (document <- xmlList) yield document}</Documents>)
      }
    )
  )

  def selectDocuments(schemaName: String, q: String) = Action {
    BaseXConnection.withSession(
      session => {
        val query =
          s"let $$all := for $$doc in ${docCollection(schemaName)}/Document" +
          s" where $$doc/Body//*[text() contains text${quote(q + ".+")} using wildcards]" +
          s" or $$doc/Body//*[text() contains text ${quote(q)} using stemming]" +
          s" order by $$doc/Header/Timestamp descending return $$doc" +
          s" return subsequence($$all, 1, $MAX_RESULTS)"
        val xmlList = session.find(query).toList
        Ok(<Documents>{for (document <- xmlList) yield document}</Documents>)
      }
    )
  }

  def saveDocument() = Action(parse.json) {
    request =>
      // todo: use parse.xml, send only XML and remove the next two lines
      val legacyXmlAttribute = (request.body \ "xml").as[String]
      val body = scala.xml.XML.loadString(legacyXmlAttribute)
      // todo: probably don't need placeholders either

      val ID_PLACEHOLDER = "#IDENTIFIER#"
      object Stamp extends RewriteRule {
        override def transform(node : Node):Node = node match {
          case elem: Elem if elem.label == "Identifier" && elem.text == ID_PLACEHOLDER =>
            elem.copy(child = Seq(Text(generateId("DOC"))))
          case elem: Elem if elem.label == "TimeStamp" =>
            elem.copy(child = Seq(Text(Platform.currentTime.toString)))
          case remainder => remainder
        }
      }
      object Stamper extends RuleTransformer(Stamp)
      val stampedBody = Stamper(body)
      val fresh = (body \ "Header" \ "Identifier").text == ID_PLACEHOLDER
      val schemaName = (body \ "Header" \ "SchemaName").text
      val identifier: String = (stampedBody \ "Header" \ "Identifier").text
      val file = s"/documents/$schemaName/$identifier.xml"
      val xmlStream = new ByteArrayInputStream(stampedBody.toString().getBytes("utf-8"))
      BaseXConnection.withSession(
        session => {
          if (fresh) {
            if (schemaName == "MediaMetadata") {
                // todo: save media, and then add document
              NotImplemented
            }
            else {
              session.add(file, xmlStream)
              Ok(stampedBody)
            }
          }
          else {
            session.replace(file, xmlStream)
            Ok(stampedBody)
          }
        }
      )
  }
}

