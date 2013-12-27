package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import storage.{BaseXConnection, BaseXBridge}

object Application extends Controller with BaseXBridge {

  def index = Action {
    Ok(views.html.index("OSCR says hello!"))
  }

  def langResponse(lang: String, session: ClientSession) = session.findOneRaw(langPath(lang)) match {
    case Some(xml) => Ok(xml)
    case None => NotFound
  }

  def getLang(lang: String) = Action(
    BaseXConnection.withSession(
      session =>
        langResponse(lang, session)
    )
  )

  def setLangLabel(lang: String) = Action(parse.json) {
    request =>
      val keyOpt = (request.body \ "key").asOpt[String]
      val labelOpt = (request.body \ "label").asOpt[String]
      (keyOpt, labelOpt) match {
        case (Some(key), Some(label)) =>
          BaseXConnection.withSession {
            session =>
              val labelPath = langPath(lang) + "/label"
              val keyPath = labelPath + "/" + key
              val command = s"if (exists($keyPath))" +
                s" then replace value of node $keyPath with ${quote(label)}" +
                s" else insert node <$key>${inXml(label)}</$key> into $labelPath"
              execute(command, session)
              langResponse(lang, session)
          }
        case _ =>
          BadRequest("Missing key or label")
      }
  }

  def setLangElement(lang: String) = Action(parse.json) {
    request =>
      ((request.body \ "key").asOpt[String], (request.body \ "title").asOpt[String], (request.body \ "doc").asOpt[String]) match {
        case (Some(key), Some(title), None) =>
          BaseXConnection.withSession {
            session =>
              val elementPath = langPath(lang) + "/element"
              val keyPath = elementPath + "/" + key
              val entryPath = keyPath + "/title"
              val command = s"if (exists($keyPath))" +
                s"then replace value of node $entryPath with ${quote(title)}" +
                s"else insert node <$key><title>${inXml(title)}</title><doc>?</doc></$key> into $elementPath"
              execute(command, session)
              langResponse(lang, session)
          }
        case (Some(key), None, Some(doc)) =>
          BaseXConnection.withSession {
            session =>
              val elementPath = langPath(lang) + "/element"
              val keyPath = elementPath + "/" + key
              val entryPath = keyPath + "/doc"
              val command = s"if (exists($keyPath))" +
                s"then replace value of node $entryPath with ${quote(doc)}" +
                s"else insert node <$key><title>?</title><doc>${inXml(doc)}</doc></$key> into $elementPath"
              execute(command, session)
              langResponse(lang, session)
          }
        case _ =>
          BadRequest("Missing key or value")
      }
  }

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
            <Documents>{
              for (schema <- List("Photo", "Video", "InMemoriam", "Location")) yield
              <Schema>
                <Name>{schema}</Name>
                <Count>{count(docCollection(schema))}</Count>
              </Schema>
              }</Documents>
          </Statistics>
        session.findOneRaw(query.toString()) match {
          case Some(xml) => Ok(xml)
          case None => NotFound
        }
    }
  }

  //  app.post('/authenticate', function (req, res) {
  //  app.post('/i18n/:lang/save', function (req, res) {
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

