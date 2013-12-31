package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import services.{BaseXController, BaseXConnection, BaseXBridge}
import play.Logger
import play.api.libs.json.JsValue
import scala.compat.Platform

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

  case class HeaderFromClient(SchemaName: String, Identifier: String, Timestamp: String, SavedBy: String, Title:String)
  case class DocumentFromClient(header: HeaderFromClient, body: JsValue, xml: String)

  def saveDocument() = Action(parse.json) {
    request =>
      val IDENTIFIER = "#IDENTIFIER#"
      val TIMESTAMP = "#TIMESTAMP#"
      val time = Platform.currentTime.toString

      val header = (request.body \ "header").as[JsValue]
      //        "header":{"SchemaName":"Photo","Identifier":"OSCR-Photo-d8lopli-s55","Title":"First","TimeStamp":"#TIMESTAMP#","SavedBy":"OSCR-US-d2siboo-zj6"},
      val body = (request.body \ "body").as[JsValue]
      //        "body":{"Photo":{"Title":"First","Type":{"Identifier":"OSCR-VO-hqebnnet-sbi","Label":"Type Two"}}},
      val xml = (request.body \ "xml").as[String]
      val xmlTimestamped = xml.replace("#TIMESTAMP#", time)
      Logger.info("Document timestamped: " + xmlTimestamped)
      // <Document>
      // <Header> <SchemaName>Photo</SchemaName> <Identifier>OSCR-Photo-d8lopli-s55</Identifier> <Title>First</Title> <TimeStamp>#TIMESTAMP#</TimeStamp> <SavedBy>OSCR-US-d2siboo-zj6</SavedBy> </Header>
      // <Body> <Photo> <Title>First</Title> <Type> <Identifier>OSCR-VO-hqebnnet-sbi</Identifier> <Label>Type Two</Label> </Type> </Photo> </Body>
      // </Document>




    
      //      Logger.info("Header: " + header)
      //      Logger.info("Body  : " + body)
      //      Logger.info("XML   : " + xml)
//      P.saveDocument = function (envelope, receiver) {
//        var s = this.services;
//        var IDENTIFIER = '#IDENTIFIER#';
//        var TIMESTAMP = '#TIMESTAMP#';
//        var time = new Date().getTime();
//        var hdr = _.clone(envelope.header);
//        var body = envelope.body;
//
//        function addDocument() {
//          var xml = envelope.xml
//            .replace(IDENTIFIER, hdr.Identifier) // header
//            .replace(IDENTIFIER, hdr.Identifier) // maybe body
//            .replace(TIMESTAMP, time); // header
//          s.add('add document ' + hdr.Identifier,
//          s.docDocument(hdr.SchemaName, hdr.Identifier),
//          xml,
//          receiver
//          );
//        }
//
//        hdr.TimeStamp = time;
//        if (hdr.Identifier === IDENTIFIER) {
//          if (envelope.header.SchemaName == 'MediaMetadata') {
//            // expects fileName, mimeType
//            log('save image');
//            log(body);
//            s.Media.saveMedia(body, function (fileName) {
//              hdr.Identifier = fileName;
//              addDocument();
//            });
//          }
//          else {
//            hdr.Identifier = util.generateDocumentId(hdr.SchemaName);
//            addDocument();
//          }
//        }
//        else {
//          // todo: move the current one to the backup collection
//          var stamped = envelope.xml.replace(TIMESTAMP, time);
//          s.replace('replace document ' + hdr.Identifier,
//          s.docDocument(hdr.SchemaName, hdr.Identifier),
//          stamped,
//          receiver
//          );
//        }
//      };

      NotImplemented
  }
}

