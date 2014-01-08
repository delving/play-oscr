package controllers

import services.BaseXController
import play.api.mvc.Action
import play.Logger

object Media extends BaseXController {

  def uploadFile = Action(parse.multipartFormData) {
    request =>
      Logger.info("Multipart upload: " + request.body)
      Ok("Multipart accepted")
  }

  def getFile(fileName: String) = Action {
    NotImplemented
  }

  def getThumbnail(fileName: String) = Action {
    NotImplemented
  }

}

