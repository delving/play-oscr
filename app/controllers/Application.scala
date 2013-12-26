package controllers

import play.api._
import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import play.api.libs.json.{JsValue, JsString, Json, JsObject}
import play.mvc.Http.Response

object Application extends Controller {

  def langDocument(lang: String) = {
    s"/i18n/$lang.xml"
  }

  def langPath(lang: String): String = {
    s"doc('oscr${langDocument(lang)}')/Language"
  }

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

  def index = Action {
    Ok(views.html.index("OSCR says hello!"))
  }

  def execute(command: String, session: ClientSession) = session.execute(
    s"<xquery><![CDATA[$command]]></xquery>"
  )

  def langResponse(lang: String, session: ClientSession) = session.findOneRaw(langPath(lang)) match {
    case Some(xml) => Ok(xml)
    case None => NotFound
  }

  def getLang(lang: String) = Action(
    BaseXConnection.withSession(
      session =>
        langResponse(lang, session)
    )
  )

  def jsonBody(request: Request[AnyContent])(block: JsValue => Result): Result = {
    request.body.asJson.map(block).getOrElse(BadRequest("Expected JSON"))
  }

  def setLangLabel(lang: String): Action[AnyContent] = Action(jsonBody(_) {
    json =>
      ((json \ "key").asOpt[String], (json \ "label").asOpt[String]) match {
        case (Some(key), Some(label)) =>
          BaseXConnection.withSession {
            session =>
              val labelPath = langPath(lang) + "/label"
              val keyPath = labelPath + "/" + key
              val command = s"if (exists($keyPath))" +
                s" then replace value of node $keyPath with ${quote(label)}" +
                s" else insert node <$key>${inXml(label)}</$key> into $labelPath"
              execute(command, session)
              langResponse(lang, session)
          }
        case _ =>
          BadRequest("Missing key or label")
      }
  })

  //  app.post('/authenticate', function (req, res) {
  //  app.post('/i18n/:lang/element', function (req, res) {
  //  app.post('/i18n/:lang/save', function (req, res) {
  //  app.get('/statistics', function (req, res) {
  //  app.get('/person/user/fetch/:identifier', function (req, res) {
  //  app.get('/person/user/select', function (req, res) {
  //  app.get('/person/user/all', function (req, res) {
  //  app.get('/person/group/fetch/:identifier', function (req, res) {
  //  app.get('/person/group/select', function (req, res) {
  //  app.get('/person/group/all', function (req, res) {
  //  app.post('/person/group/save', function (req, res) {
  //  app.get('/person/group/:identifier/users', function (req, res) {
  //  app.post('/person/group/:identifier/add', function (req, res) {
  //  app.post('/person/group/:identifier/remove', function (req, res) {
  //  app.get('/vocabulary/:vocab', function (req, res) {
  //  app.get('/vocabulary/:vocab/all', function (req, res) {
  //  app.get('/vocabulary/:vocab/select', function (req, res) {
  //  app.get('/vocabulary/:vocab/fetch/:identifier', function (req, res) {
  //  app.post('/vocabulary/:vocab/add', function (req, res) {
  //  app.get('/document/schema/:schema', function (req, res) {
  //  app.get('/document/fetch/:schema/:identifier', function (req, res) {
  //  app.get('/document/list/documents/:schema', function (req, res) {
  //  app.get('/document/select/:schema', function (req, res) {
  //  app.post('/document/save', function (req, res) {
  //  app.get('/media/fetch/:fileName', function (req, res) {
  //  app.get('/media/thumbnail/:fileName', function (req, res) {
  //  app.get('/log', function (req, res) {
  //  app.get('/refreshSchemas', function (req, res) {
  //  app.get('/snapshot/:fileName', function (req, res) {
  //  app.get('/snapshot', function (req, res) {
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