package controllers

import services.{BaseXConnection, BaseXController}
import org.basex.server.ClientSession
import play.api.mvc.Action

object Language extends BaseXController {

  def langResponse(lang: String, session: ClientSession) = findOneResult(langPath(lang), session)

  def getLang(lang: String) = Action(
    BaseXConnection.withSession(langResponse(lang, _))
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
      val keyOpt: Option[String] = (request.body \ "key").asOpt[String]
      val titleOpt: Option[String] = (request.body \ "title").asOpt[String]
      val docOpt: Option[String] = (request.body \ "doc").asOpt[String]
      (keyOpt, titleOpt, docOpt) match {
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

  def saveLanguage(lang: String) = Action {
    NotImplemented
  }

}

