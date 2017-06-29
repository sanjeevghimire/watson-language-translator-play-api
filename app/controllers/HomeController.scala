package controllers

import javax.inject._

import models.TranslationData
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import services.WatsonLanguageTranslator

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, ws: WatsonLanguageTranslator) extends AbstractController(cc) with I18nSupport {


  val translateForm = Form(
    mapping(
      "text" -> nonEmptyText,
      "translateFrom" -> nonEmptyText,
      "translateTo" -> nonEmptyText
    )(TranslationData.apply)(TranslationData.unapply)
  )


  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def translate() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.translate(translateForm))
  }


  def translation() = Action { implicit request: Request[AnyContent] =>

//    val userData = request.body;
//
//    ws.getTranslatedTextFromWatson()
//
//    Redirect(routes.HomeController.translate).flashing("Translated Text" -> "Hola, mundo!")


    translateForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.translate(translateForm))
      },
      translate => {
        val translatedText = ws.getTranslatedTextFromWatson(translate.text,translate.translateFrom,translate.translateTo)
        Redirect(routes.HomeController.translate).flashing("Translated Text" -> translatedText)
      }
    )

  }


}
