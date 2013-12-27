package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import storage.{BaseXConnection, BaseXBridge}

object Person extends Controller with BaseXBridge {

  //  app.get('/person/user/select', function (req, res) {
  //  app.get('/person/user/all', function (req, res) {
  //  app.get('/person/group/fetch/:identifier', function (req, res) {
  //  app.get('/person/group/select', function (req, res) {
  //  app.get('/person/group/all', function (req, res) {
  //  app.post('/person/group/save', function (req, res) {
  //  app.get('/person/group/:identifier/users', function (req, res) {
  //  app.post('/person/group/:identifier/add', function (req, res) {
  //  app.post('/person/group/:identifier/remove', function (req, res) {

  def fetchPerson(identifier: String) = Action(
    BaseXConnection.withSession(
      session =>
        session.findOneRaw(userPath(identifier)) match {
          case Some(xml) => Ok(xml)
          case None => NotFound
        }
    )
  )
}

