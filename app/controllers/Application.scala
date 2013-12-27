package controllers

import play.api.mvc._
import eu.delving.basex.client._
import storage.{BaseXController, BaseXConnection}

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

  def authenticate() = Action(parse.json) {
    request =>
// LOOK IN CULTUREHUB
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
//              storage.Person.getOrCreateUser(profile, function (xml) {
//                req.session.Identifier = util.getFromXml(xml, 'Identifier');
//                res.xml(xml);
//                storage.Log.add(req, {
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
      NotImplemented
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
//      storage.snapshotCreate(function (localFile) {
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
//      res.redirect('/snapshot/'+storage.snapshotName());
//    });
    NotImplemented
  }
}

