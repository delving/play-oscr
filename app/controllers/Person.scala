package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import storage.{BaseXConnection, BaseXBridge}

object Person extends Controller with BaseXBridge {

	//  app.post('/person/group/:identifier/add', function (req, res) {
	//  app.post('/person/group/:identifier/remove', function (req, res) {

	def resultForQuery(query: String, session: ClientSession): Result = {
		session.findOne(query) match {
			case Some(xml) => Ok(xml)
			case None => NotFound
		}
	}

	//  app.get('/person/user/select', function (req, res) {
	def getUser(identifier: String) = Action(
		BaseXConnection.withSession(
			session => resultForQuery(userPath(identifier), session)
		)
	)


	//  app.get('/person/group/fetch/:identifier', function (req, res) {
	def fetchGroup(identifier: String) = Action {
		BaseXConnection.withSession {
			session => resultForQuery(groupPath(identifier), session)
		}
	}

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

	//  app.get('/person/group/select', function (req, res) {
	def selectGroup(q: String) = Action {
		BaseXConnection.withSession {
			session =>
			  	val query =
					<Groups>
						{{ {groupCollection}[contains(lower-case(Name), lower-case({quote(q.toLowerCase)}))] }}
					</Groups>

				resultForQuery(query.toString(), session)
		}
	}

	//  app.get('/person/group/all', function (req, res) {
	def selectAllGroups = Action {
		BaseXConnection.withSession {
			session =>
				val query =
					<Groups>
						{{ {groupCollection} }}
					</Groups>

				resultForQuery(query.toString(), session)
		}
	}

	//  app.post('/person/group/save', function (req, res) {


	//  app.get('/person/group/:identifier/users', function (req, res) {
	def getUsersInGroup(identifier: String) = Action {
		BaseXConnection.withSession {
			session =>
				val query =
					<Users>
						{{ {userCollection}[Memberships/Membership/GroupIdentifier={quote(identifier)}] }}
					</Users>

				resultForQuery(query.toString(), session)
		}
	}
}

