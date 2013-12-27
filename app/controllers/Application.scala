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

  def authenticate() = play.mvc.Results.TODO

  def getLog() = play.mvc.Results.TODO

  def refreshSchemas() = play.mvc.Results.TODO

  def createSnapshot(fileName: String) = {
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
    play.mvc.Results.TODO
  }

  def createSnapshotNow() = {
//    this.snapshotName = function() {
//      var now = new Date();
//      var dateString = now.getFullYear() + '-' + (now.getMonth() + 1) + '-' + now.getDate() +
//        '-' + now.getHours() + '-' + now.getMinutes();
//      return 'OSCR-Snapshot-' + dateString;
//    };
//    app.get('/snapshot', function (req, res) {
//      res.redirect('/snapshot/'+storage.snapshotName());
//    });
    play.mvc.Results.TODO
  }
}

