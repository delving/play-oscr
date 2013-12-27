package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import storage.{BaseXController, BaseXConnection, BaseXBridge}
import play.Logger

object Document extends BaseXController {

  val MAX_RESULTS = 30

  //  app.get('/document/select/:schema', function (req, res) {
  //  app.post('/document/save', function (req, res) {

  def getDocumentSchema(schemaName: String) = Action(
    BaseXConnection.withSession(findOneResult(s"$schemaPath/Document/$schemaName", _))
  )

  def getDocument(schemaName: String, identifier: String) = Action(
    BaseXConnection.withSession(findOneResult(docPath(schemaName, identifier), _))
  )

  def listDocuments(schemaName: String) = Action(
    BaseXConnection.withSession(
      session => {
        val query = s"let $$all := for $$doc in ${docCollection(schemaName)}" +
          " order by $doc/Header/Timestamp descending return $doc" +
          s" return subsequence($$all, 1, $MAX_RESULTS)"
        Logger.info(query)
        val xmlList = session.find(query).toList
        Ok(<Documents>{for (document <- xmlList) yield document}</Documents>)
      }
    )
  )

}

