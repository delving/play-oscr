package controllers

import play.api.mvc._
import eu.delving.basex.client._
import services.{BaseXController, BaseXConnection}
import java.util.Date

object Person extends BaseXController {

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

  def selectUsers(search: String) = Action {
      var quoteSearch = quote(search)
      var query =
        s"$userCollection[contains(lower-case(Profile/username), $quoteSearch) " +
        s"or contains(lower-case(Profile/email), $quoteSearch) " +
        s"or contains(lower-case(Profile/firstName), $quoteSearch) " +
        s"or contains(lower-case(Profile/lastName), $quoteSearch)]"
      BaseXConnection.withSession {
        session =>
          var xmlList = session.find(query).toList
          Ok(<Users>{for (user <- xmlList) yield user}</Users>)
      }
  }

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

  //  app.get('/person/group/:identifier/users', function (req, res) {
	def getUsersInGroup(groupIdentifier: String) = Action {
		BaseXConnection.withSession {
			session =>
				val query = <Users>{{ {userCollection}[Memberships/Membership/GroupIdentifier={quote(groupIdentifier)}] }}</Users>
        findOneResult(query.toString(), session)
		}
	}

  def saveGroup = Action(parse.json) {
    request =>
      // todo: the group should probably be sent as XML
//      P.saveGroup = function (group, receiver) {
//        var s = this.services;
//        group.SaveTime = new Date().getTime();
//        var existing = group.Identifier;
//        if (!existing) {
//          group.Identifier = util.generateGroupId();
//        }
//        var groupXml = util.objectToXml(group, "Group");
//        if (existing && group.Identifier != 'OSCR') {
//          s.replace('save existing group ' + group.Identifier,
//          s.groupDocument(group.Identifier), groupXml,
//          receiver
//          );
//        }
//        else { // here we could try fuzzy match or something
//          log('search for ' + group.Name);
//          s.query('check group',
//          s.groupCollection() + '[Name = ' + util.quote(group.Name) + ']',
//          function(result) {
//            log(group.Name + ' result was '+result);
//            if (result.length == 0) { // text is not found
//              s.add('add group ' + group.Identifier,
//              s.groupDocument(group.Identifier), groupXml,
//              receiver
//              );
//            }
//            else {
//              receiver(null);
//            }
//          }
//          );
//        }
//      };
//      val group = request.body
//      group \ "SaveTime" = new Date().getTime
//      group \ "Identifier" = (group \ "Identifier").asOpt[String] getOrElse ""
      NotImplemented
  }

  def addUserToGroup(groupIdentifier: String) = Action(parse.json) {
    request =>
      val userIdentifier = (request.body \ "userIdentifier").as[String]
      val userRole = (request.body \ "userRole").as[String]
      val command =
        s"let $$user := ${userPath(userIdentifier)} "+
        s"let $$mem := <Membership><GroupIdentifier>$groupIdentifier</GroupIdentifier><Role>$userRole</Role></Membership> "
        s"return "+
        s"if (exists($$user/Memberships/Membership[GroupIdentifier=${quote(groupIdentifier)}] "+
        "then ()"+
        "if (exists($user/Memberships) "+
        "then (insert node $mem into $user/Memberships) "+
        "else (insert node <Memberships>{$mem}</Memberships> into $user))"
      BaseXConnection.withSession(
        session =>
          execute(command, session)
          // todo: respond with user
      )
      NotImplemented
  }

  def removeUserFromGroup(groupIdentifier: String) = Action(parse.json) {
    request =>
      val userIdentifier = (request.body \ "userIdentifier").as[String]
      val command = s"delete node ${userPath(userIdentifier)}/Memberships/Membership[GroupIdentifier=${quote(groupIdentifier)}]"
      BaseXConnection.withSession(
        session =>
          execute(command, session)
          // todo: respond with user
      )
      NotImplemented
  }

}

