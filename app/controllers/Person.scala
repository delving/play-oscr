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

  def selectUsers(q: String) = Action {
//    P.getUsers = function (search, receiver) {
//      var s = this.storage;
//      s.query('get users ' + search,
//      [
//      '<Users>',
//      '    { ' + s.userCollection() + '[',
//        '      contains(lower-case(Profile/username), ' + util.quote(search) + ')',
//        '      or contains(lower-case(Profile/email), ' + util.quote(search) + ')',
//        '      or contains(lower-case(Profile/firstName), ' + util.quote(search) + ')',
//        '      or contains(lower-case(Profile/lastName), ' + util.quote(search) + ')',
//        '    ]}',
//      '</Users>'
//      ],
//      receiver
//      );
//    };
    NotImplemented
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
	def getUsersInGroup(identifier: String) = Action {
		BaseXConnection.withSession {
			session =>
				val query = <Users>{{ {userCollection}[Memberships/Membership/GroupIdentifier={quote(identifier)}] }}</Users>
        findOneResult(query.toString(), session)
		}
	}

  def saveGroup = Action(parse.json) {
    request =>
//      P.saveGroup = function (group, receiver) {
//        var s = this.storage;
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

  def addUserToGroup(identifier: String) = Action(parse.json) {
    request =>
//      P.addUserToGroup = function (userIdentifier, role, groupIdentifier, receiver) {
//        var s = this.storage;
//        var addition = userIdentifier + ' ' + role + ' ' + groupIdentifier;
//        s.update('add user to group ' + addition,
//        [
//        'let $user := ' + s.userPath(userIdentifier),
//        'let $mem := ' + '<Membership><GroupIdentifier>' + groupIdentifier + '</GroupIdentifier><Role>' + role + '</Role></Membership>',
//        'return',
//        'if (exists($user/Memberships/Membership[GroupIdentifier=' + util.quote(groupIdentifier) + ']))',
//        'then ()',
//        'else ( if (exists($user/Memberships))',
//        'then (insert node $mem into $user/Memberships)',
//        'else (insert node <Memberships>{$mem}</Memberships> into $user))'
//        ],
//        function (result) {
//          if (result) {
//            s.query('re-fetch user ' + addition,
//            s.userPath(userIdentifier),
//            receiver
//            );
//          }
//          else {
//            receiver(null);
//          }
//        }
//        );
//      };
      NotImplemented
  }

  def removeUserFromGroup(identifier: String) = Action(parse.json) {
    request =>
//      P.removeUserFromGroup = function (userIdentifier, role, groupIdentifier, receiver) {
//        var s = this.storage;
//        var addition = userIdentifier + ' ' + role + ' ' + groupIdentifier;
//        s.update('remove user from group ' + addition,
//        'delete node ' + s.userPath(userIdentifier) + '/Memberships/Membership[GroupIdentifier=' + util.quote(groupIdentifier) + ']',
//        function (result) {
//          if (result) {
//            s.query('re-fetch user after remove membership ' + addition,
//            s.userPath(userIdentifier),
//            receiver
//            );
//          }
//          else {
//            receiver(null);
//          }
//        }
//        );
//      };
      NotImplemented
  }

}

