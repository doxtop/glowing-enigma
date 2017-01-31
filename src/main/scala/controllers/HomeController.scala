package controllers

import play.api.mvc.{Action, Controller}

/**
  * Renders a home page.
  */
class HomeController extends Controller {

  def index = Action { implicit request =>
    Ok(html.index())
  }
}