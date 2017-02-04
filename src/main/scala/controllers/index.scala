package controllers

import play.api.mvc.{Action,Controller}

/** Serve index page. */
class Index() extends Controller {
  def index = Action{r => Ok(html.index())}
}
