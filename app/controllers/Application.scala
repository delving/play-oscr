package controllers

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import eu.delving.basex.client._
import services.{MissingLibs, BaseXController, BaseXConnection}
import play.api.libs.Crypto
import play.api.libs.ws.{Response, WS}
import play.mvc.Http
import play.Logger
import scala.concurrent.Future
import scala.util.Success

object Application extends BaseXController {

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

      commonsRequest(s"/user/authenticate/$hash").flatMap {
        response =>
          response.status match {
            case Http.Status.OK =>
              commonsRequest(s"/user/profile/$username").map(profileResponse => Ok(profileResponse.body))
            case _ =>
              Future(Unauthorized("Username password didn't work, dude"))
          }
      }
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

