package controllers

import play.api.mvc._
import eu.delving.basex.client._
import org.basex.server.ClientSession
import storage.{BaseXController, BaseXConnection, BaseXBridge}
import play.Logger

object Vocabulary extends BaseXController {

  val MAX_RESULTS = 30

  def getVocabularySchema(schemaName: String) = Action(
    BaseXConnection.withSession {
      session =>
        val entryElement = s"$schemaPath/Vocabulary/$schemaName"
        val entry = s"<Entry>{ $entryElement/text() }<Label/><Identifier/>{ $entryElement/* }</Entry>"
        val query = s"<$schemaName>$entry</$schemaName>"
      Logger.info(query)
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

  def getVocabularyEntry(schemaName: String, identifier: String) = play.mvc.Results.TODO

  def selectFromVocabulary(schemaName: String, q: String) = play.mvc.Results.TODO

  def addVocabularyEntry(schemaName: String) = play.mvc.Results.TODO
}

