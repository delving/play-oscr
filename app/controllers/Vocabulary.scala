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

  def addVocabularyEntry(schemaName: String) = play.mvc.Results.TODO
}

