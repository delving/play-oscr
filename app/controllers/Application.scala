package controllers

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import eu.delving.basex.client._
import services.{MissingLibs, BaseXController, BaseXConnection}
import play.api.libs.Crypto
import play.api.libs.ws.{Response, WS}
import play.mvc.Http
import play.api.libs.ws.WS.WSRequestHolder
import play.api.libs.json.JsValue
import play.Logger
import scala.concurrent.Future

object Application extends BaseXController {

  def index = Action(Ok(views.html.index("OSCR says hello!")))

  def getStatistics = Action {
    BaseXConnection.withSession {
      session =>
        def count(collection:String) = execute(s"count($collection)", session)
        val query =
          <Statistics>
            <People>
              <Person>{count(userCollection)}</Person>
              <Group>{count(groupCollection)}</Group>
            </People>
            <Documents>
              {for (schema <- List("Photo", "Video", "InMemoriam", "Location")) yield
              <Schema>
                <Name>{schema}</Name>
                <Count>{count(docCollection(schema))}</Count>
              </Schema>}
            </Documents>
          </Statistics>
        session.findOne(query.toString()) match {
          case Some(xml) => Ok(xml)
          case None => NotFound
        }
    }
  }
  
  def commonsRequest(path: String): Future[Response] = {
    val url: String = s"https://commons.delving.eu$path"
    Logger.info("request:" + url)
    WS.url(url)
      .withQueryString(
        ("apiToken", "6f941a84-cbed-4140-b0c4-2c6d88a581dd"),
        ("apiOrgId", "delving"),
        ("apiNode", "playground") // todo: change to OSCR
      )
      .get()
  }

  def authenticate() = Action.async(parse.urlFormEncoded) {
    request =>
      var username = request.body("username").head
      var password = request.body("password").head
      val hashedPassword = MissingLibs.passwordHash(password, MissingLibs.HashType.SHA512)
      val hash = Crypto.sign(hashedPassword, username.getBytes("utf-8"))
      commonsRequest(s"/user/authenticate/$hash").map {
        authResponse =>
          authResponse.status match {
            case Http.Status.OK =>
              Logger.info("received ok from auth request")
//              val profileFuture: Future[Response] = commonsRequest(s"/user/profile/$username")
//              profileFuture.map {
//                profileResponse =>
//                  profileResponse.status match {
//                    case Http.Status.OK =>
//                      Logger.info("got OK response for profile")
//                      Logger.info(profileResponse.json.toString())
//                      Ok(profileResponse.json.toString())
//                    case _ =>
//                      Logger.info("no OK response for profile")
//                      Ok("Shit")
//                  }
//              }
              Ok("Looks good")
            case _ =>
//              Unauthorized("Didn't get the right response for authenticate")
              Ok("Crap")
          }
      }

//      get("/user/authenticate/" + URLEncoder.encode(hash, "utf-8")).map {
//        response => response.status == OK
//      }.getOrElse(false)
//      function commonsQueryString() {
//        var API_QUERY_PARAMS = {
//          "apiToken": "6f941a84-cbed-4140-b0c4-2c6d88a581dd",
//          "apiOrgId": "delving",
//          "apiNode": "playground"
//        };
//        var queryParams = [];
//        for (var key in API_QUERY_PARAMS) {
//          queryParams.push(key + '=' + API_QUERY_PARAMS[key]);
//        }
//        return queryParams.join('&');
//      }
//      function commonsRequest(path) {
//        return {
//          method: "GET",
//          host: 'commons.delving.eu',
//          port: 443,
//          path: path + '?' + commonsQueryString()
//        }
//      }
//   app.post('/authenticate', function (req, res) {
//      var username = req.body.username;
//      var password = req.body.password;
//      var sha = crypto.createHash('sha512');
//      var hashedPassword = sha.update(new Buffer(password, 'utf-8')).digest('base64');
//      var hmac = crypto.createHmac('sha1', username);
//      var hash = hmac.update(hashedPassword).digest('hex');
//      res.setHeader('Content-Type', 'text/xml');
//      https.request(
//        commonsRequest('/user/authenticate/' + hash),
//      function (authResponse) {
//        if (authResponse.statusCode == 200) {
//          https.request(
//            commonsRequest('/user/profile/' + username),
//          function (profileResponse) {
//            var data;
//            profileResponse.on('data', function (data) {
//              var profile = JSON.parse(data);
//              profile.username = username;
//              req.session.profile = profile;
//              services.Person.getOrCreateUser(profile, function (xml) {
//                req.session.Identifier = util.getFromXml(xml, 'Identifier');
//                res.xml(xml);
//                services.Log.add(req, {
//                  Op: "Authenticate"
//                });
//              });
//            });
//          }
//          ).end();
//        }
//        else {
//          res.send("<Error>Failed to authenticate</Error>");
//        }
//      }
//      ).end();
//    });
//      NotImplemented
  }

  def getLog = Action(
    BaseXConnection.withSession(findOneResult(logPath, _))
  )

  def refreshSchemas() = Action(
//    this.refreshSchemas = function (receiver) {
//      var session = this.session;
//      var schemaFile = this.directories.schemaFile;
//      var fileName = path.basename(schemaFile);
//      var contents = fs.readFileSync(schemaFile, 'utf8');
//      session.replace('/' + fileName, contents, function (error, reply) {
//        if (reply.ok) {
//          console.log("Reloaded: " + fileName);
//        }
//        else {
//          console.error('Unable to refresh schemas');
//          console.error(error);
//        }
//        receiver();
//      });
//    };
    NotImplemented
  )

  def createSnapshot(fileName: String) = Action {
//    this.snapshotCreate = function (receiver) {
//      var snapshotDir = this.snapshotName();
//      var exportPath = this.directories.snapshot + '/' + snapshotDir;
//      var zipFile = exportPath + '.zip';
//      this.session.execute('export ' + exportPath, function () {
//        var output = fs.createWriteStream(zipFile);
//        var archive = archiver('zip');
//
//        archive.on('error', function (err) {
//          throw err;
//        });
//        output.on('close', function () {
//          receiver(zipFile);
//        });
//
//        archive.pipe(output);
//
//        function rmdir(dir) {
//          var list = fs.readdirSync(dir);
//          _.each(list, function (entry) {
//            if (entry[0] != '.') {
//              var fileName = path.join(dir, entry);
//              var stat = fs.statSync(fileName);
//              if (stat.isDirectory()) {
//                rmdir(fileName);
//              }
//              else {
//                fs.unlinkSync(fileName);
//              }
//            }
//          });
//          fs.rmdirSync(dir);
//        }
//
//        function appendToArchive(dir, zipPath) {
//          var list = fs.readdirSync(dir);
//          _.each(list, function (entry) {
//            if (entry[0] != '.') {
//              var fileName = path.join(dir, entry);
//              var stat = fs.statSync(fileName);
//              var zipFileName = zipPath + '/' + entry;
//              if (stat.isDirectory()) {
//                appendToArchive(fileName, zipFileName);
//              }
//              else {
//                archive.append(fs.createReadStream(fileName), { name: zipFileName });
//              }
//            }
//          });
//        }
//
//        appendToArchive(exportPath, snapshotDir);
//        rmdir(exportPath);
//
//        archive.finalize(function (err, bytes) {
//          if (err) {
//            throw err;
//          }
//          console.log(zipFile + ': ' + bytes + ' total bytes');
//        });
//
//      })
//    };
//  }
//    app.get('/snapshot/:fileName', function (req, res) {
//      services.snapshotCreate(function (localFile) {
//        console.log("sending " + localFile);
//        res.sendfile(localFile);
//      });
//    });
    NotImplemented
  }

  def createSnapshotNow() = Action {
//    this.snapshotName = function() {
//      var now = new Date();
//      var dateString = now.getFullYear() + '-' + (now.getMonth() + 1) + '-' + now.getDate() +
//        '-' + now.getHours() + '-' + now.getMinutes();
//      return 'OSCR-Snapshot-' + dateString;
//    };
//    app.get('/snapshot', function (req, res) {
//      res.redirect('/snapshot/'+services.snapshotName());
//    });
    NotImplemented
  }
}

