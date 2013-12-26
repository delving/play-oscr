package controllers

import play.api._
import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import play.api.libs.json.{JsString, Json, JsObject}

object Application extends Controller {

	def langDocument(lang: String) = {
		"/i18n/" + lang + ".xml"
	}

	def langPath(lang: String) = {
		"doc('oscr" + langDocument(lang) + "')/Language"
	}

	def index = Action {
		Ok(views.html.index("OSCR says hello!"))
	}

	def getLang(lang: String) = Action {

		val query = langPath(lang)

		BaseXConnection.withSession {
			session =>
				session.findOneRaw(query) match {
					case Some(xml) => Ok(xml)
					case None => NotFound
				}
		}
	}

	def labelLang(lang: String) = Action {
		request =>
			val reqObject = request.body.asJson.get.as[JsObject].value
			val keyOpt = reqObject.get("key")
			keyOpt match {
				case Some(key) => {
					val newLabel = reqObject("label")
					updateLabel(lang, key.toString(), newLabel.toString())
					Redirect("/i18n/" + lang)
				}
				case None => NotFound
			}
	}

	private def updateLabel(lang: String, key: String, value: String) {
		val labelPath = langPath(lang) + "/label"
		val keyPath = labelPath + "/" + key
		BaseXConnection.withSession {
			session =>
				val command =
					"if (exists(" + keyPath + "))" +
					  "then replace value of node " + keyPath + " with " + util.quote(value) +
					  "else insert node <" + key + ">" + util.inXml(value) + "</" + key + "> into " + labelPath
				Logger.info(command)
				session.execute("<xquery><![CDATA[\n" + command + "\n]]></xquery>")
		}
	}

	object util {
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
	}

	//  app.post('/authenticate', function (req, res) {
	//  app.get('/i18n/:lang', function (req, res) { DONE
	//  app.post('/i18n/:lang/element', function (req, res) {
	//  app.post('/i18n/:lang/label', function (req, res) {
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