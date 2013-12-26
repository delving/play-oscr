package controllers

import play.api._
import play.api.mvc._
import eu.delving.basex.client._

object Application extends Controller {

	def index = Action {
		Ok(views.html.index("OSCR says hello!"))
	}

	def getLang(lang: String) = Action {
		def langDocument(lang: String) = {
			"/i18n/" + lang + ".xml"
		}

		def langPath(lang: String) = {
			"doc('oscr" + langDocument(lang) + "')/Language"
		}

		val query = langPath(lang)

		Ok(s"<xml>$query</xml>")
	}
	//  app.post('/authenticate', function (req, res) {
	//  app.get('/i18n/:lang', function (req, res) {
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
	val server = new BaseX(host = "localhost", port = 1985, eport = 1986, user = "admin", pass = "admin")
}