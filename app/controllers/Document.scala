package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import services.{BaseXController, BaseXConnection, BaseXBridge}
import play.Logger

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
      Logger.info(request.body.toString())
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

