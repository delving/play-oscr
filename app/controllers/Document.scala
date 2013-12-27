package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import storage.{BaseXConnection, BaseXBridge}
import play.Logger

object Document extends Controller with BaseXBridge {

  val MAX_RESULTS = 30

  //  app.get('/document/select/:schema', function (req, res) {
  //  app.post('/document/save', function (req, res) {

  def getDocumentSchema(schemaName: String) = Action(
    BaseXConnection.withSession(
      session =>
        session.findOne(s"$schemaPath/Document/$schemaName") match {
          case Some(xml) => Ok(xml)
          case None => NotFound
        }
    )
  )

  def getDocument(schemaName: String, identifier: String) = Action(
    BaseXConnection.withSession(
      session =>
        session.findOne(docPath(schemaName, identifier)) match {
          case Some(xml) => Ok(xml)
          case None => NotFound
        }
    )
  )

  def listDocuments(schemaName: String) = Action(
    BaseXConnection.withSession(
      session => {
        val query = s"let $$all := for $$doc in ${docCollection(schemaName)}" +
          " order by $doc/Header/Timestamp descending return $doc" +
          s" return subsequence($$all, 1, $MAX_RESULTS)"
        Logger.info(query)
        val xmlList = session.find(query).toList
        Ok(
          <Documents>
            {for (document <- xmlList) yield document}
          </Documents>
        )
      }
    )
  )

}

