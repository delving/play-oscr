package controllers

import play.api.mvc._
import eu.delving.basex.client._
import storage.{BaseXController, BaseXConnection}
import java.util.Date

object Person extends BaseXController {

	//  app.post('/person/group/:identifier/add', function (req, res) {
	//  app.post('/person/group/:identifier/remove', function (req, res) {

	def getUser(identifier: String) = Action(
		BaseXConnection.withSession(findOneResult(userPath(identifier), _))
	)

	def fetchGroup(identifier: String) = Action {
		BaseXConnection.withSession(findOneResult(groupPath(identifier), _))
	}

	def getAllUsers = Action(
		BaseXConnection.withSession(
			session => {
				val xmlList = session.find(userCollection).toList
				Ok(<Users>{for (user <- xmlList) yield user}</Users>)
			}
		)
	)

	def selectGroup(q: String) = Action {
		BaseXConnection.withSession {
			session =>
        val query = <Groups>{{ {groupCollection}[contains(lower-case(Name), lower-case({quote(q.toLowerCase)}))] }}</Groups>
        findOneResult(query.toString(), session)
		}
	}

	//  app.get('/person/group/all', function (req, res) {
	def selectAllGroups = Action {
		BaseXConnection.withSession {
			session =>
				val query = <Groups>{{ {groupCollection} }}</Groups>
        findOneResult(query.toString(), session)
		}
	}

	//  app.post('/person/group/save', function (req, res) {
  def saveGroup() = play.mvc.Results.TODO
//  def saveGroup() = Action(parse.json) {
//    request =>
//      val group = request.body
//      group \ "SaveTime" = new Date().getTime
//      group \ "Identifier" = (group \ "Identifier").asOpt[String] getOrElse ""
//      NotFound
//  }

  //  app.get('/person/group/:identifier/users', function (req, res) {
	def getUsersInGroup(identifier: String) = Action {
		BaseXConnection.withSession {
			session =>
				val query = <Users>{{ {userCollection}[Memberships/Membership/GroupIdentifier={quote(identifier)}] }}</Users>
        findOneResult(query.toString(), session)
		}
	}

  def selectUsers(q: String) = play.mvc.Results.TODO

  def addGroup(identifier: String) = play.mvc.Results.TODO

  def removeGroup(identifier: String) = play.mvc.Results.TODO
}

