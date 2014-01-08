package services

import java.io.File
import play.api.Play

object FileRepo {

  val root = Play.current.configuration.getString("file.repo") match {
    case Some(directory) =>
      if (directory.startsWith("/"))
        new File(directory)
      else
        new File(s"${System.getProperty("user.home")}/$directory")
    case None =>
      new File("/tmp/OSCR")
  }

  def getRepoRoot = {
    root.getAbsolutePath
  }

  // todo: get the location from configuration
}
