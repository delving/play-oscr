package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import storage.{BaseXConnection, BaseXBridge}

object Person extends Controller with BaseXBridge {

	//  app.get('/person/group/all', function (req, res) {
	//  app.post('/person/group/save', function (req, res) {
	//  app.get('/person/group/:identifier/users', function (req, res) {
	//  app.post('/person/group/:identifier/add', function (req, res) {
	//  app.post('/person/group/:identifier/remove', function (req, res) {


	//  app.get('/person/user/select', function (req, res) {
	def getUser(identifier: String) = Action(
		BaseXConnection.withSession(
			session =>
				session.findOne(userPath(identifier)) match {
					case Some(xml) => Ok(xml)
					case None => NotFound
				}
		)
	)


	//  app.get('/person/user/all', function (req, res) {
	def getAllUsers = Action(
		BaseXConnection.withSession(
			session => {
				val xmlList = session.find(userCollection).toList
				Ok(
					<Users>
						{for (user <- xmlList) yield user}
					</Users>
				)
			}
		)
	)

	//  app.get('/person/group/fetch/:identifier', function (req, res) {
	def fetchGroup(identifier: String) = Action {
		BaseXConnection.withSession {
			session =>
			  session.findOne(groupPath(identifier)) match {
				  case Some(xml) => Ok(xml)
				  case None => NotFound
			  }
		}
	}

	//  app.get('/person/group/select', function (req, res) {
	def selectGroup(q: String) = Action {
		BaseXConnection.withSession {
			session =>
			  	val query =
					<Groups>
						{{ {groupCollection}[contains(lower-case(Name), lower-case({quote(q.toLowerCase)}))] }}
					</Groups>

				session.findOne(query.toString()) match {
					case Some(xml) => Ok(xml)
					case None => NotFound
				}
		}
	}

}

