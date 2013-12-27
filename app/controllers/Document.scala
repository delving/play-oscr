package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import storage.{BaseXConnection, BaseXBridge}

object Document extends Controller with BaseXBridge {

  //  app.get('/document/fetch/:schema/:identifier', function (req, res) {
  //  app.get('/document/list/documents/:schema', function (req, res) {
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
  
}

