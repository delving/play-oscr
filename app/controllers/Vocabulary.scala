package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import storage.{BaseXController, BaseXConnection, BaseXBridge}
import play.Logger
import play.api.libs.json.JsValue

object Vocabulary extends BaseXController {

  val MAX_RESULTS = 30

  def getVocabularySchema(schemaName: String) = Action(
    BaseXConnection.withSession {
      session =>
        val entryElement = s"$schemaPath/Vocabulary/$schemaName"
        val entry = s"<Entry>{ $entryElement/text() }<Label/><Identifier/>{ $entryElement/* }</Entry>"
        val query = s"<$schemaName>$entry</$schemaName>"
        findOneResult(query, session)
    }
  )

  def getVocabulary(schemaName: String) = Action(
    BaseXConnection.withSession(
      session => {
        val query = s"${vocabPath(schemaName)}/Entries"
        val xmlList = session.find(query).toList
        Ok(<Entries>{for (document <- xmlList) yield document}</Entries>)
      }
    )
  )

  def getVocabularyEntry(schemaName: String, identifier: String) = Action{
    BaseXConnection.withSession {
      session =>
        val query = s"${vocabPath(schemaName)}/Entries/Entry[Identifier=${quote(identifier)}]"
        findOneResult(query, session)
    }
  }

  def selectFromVocabulary(schemaName: String, q: String) = Action{
    BaseXConnection.withSession(
      session => {
        val query = s"${vocabPath(schemaName)}/Entries/Entry[contains(lower-case(Label), lower-case(${quote(q)}))]"
        val xmlList = session.find(query).toList
        Ok(<Entries>{for (document <- xmlList) yield document}</Entries>)
      }
    )
  }

  def addVocabularyEntry(schemaName: String) = Action(parse.json) {
    request =>
      val entry: JsValue = request.body \ "Entry"
      val identifierOpt = (entry \ "Identifier").asOpt[String]
      val labelOpt = (entry \ "Label").asOpt[String]
      val label = labelOpt.getOrElse("?")
      // todo: they should be sending us the XML for the entry
      BaseXConnection.withSession(
        session =>
          identifierOpt match {
            case Some(identifier) =>
              val entryPath = s"${vocabPath(schemaName)}/Entries/Entry[Identifier=${quote(identifier)}]"
              val entryXml =
                <Entry>
                  <Identifier>{identifier}</Identifier>
                  <Label>{label}</Label>
                </Entry>
              val update = s"replace node $entryPath with $entryXml"
              execute(update, session)
              Ok(entryXml)
            case None =>
              val identifier = generateId("VO")
              val entryXml =
                <Entry>
                  <Identifier>{identifier}</Identifier>
                  <Label>{label}</Label>
                </Entry>
              val entries = <Entries>{entryXml}</Entries>
              val update =
                s"if (${vocabExists(schemaName)}) then "+
                s"insert node ($entryXml) into ${vocabPath(schemaName)}/Entries "+
                s"else ${vocabAdd(schemaName, entries.toString())}"
              execute(update, session)
              Ok(entryXml)
          }
      )
  }
}
