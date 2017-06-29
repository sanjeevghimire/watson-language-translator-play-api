package services

import java.util.concurrent.TimeUnit
import javax.inject._

import play.api.libs.ws._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by sanjeevghimire on 6/27/17.
  */

@Singleton
class WatsonLanguageTranslator @Inject() ( implicit ec: ExecutionContext,ws: WSClient, configuration: play.api.Configuration) {

    val url= configuration.underlying.getString("languageTranslator.url")
    val username= configuration.underlying.getString("languageTranslator.username")
    val password=configuration.underlying.getString("languageTranslator.password")


  def getTranslatedTextFromWatson(text: String, translateFrom:String, translateTo: String) : String = {
    import play.api.libs.json._
    import play.api.libs.ws._

    val data = Json.obj(
      "text" -> text,
      "source" -> translateFrom,
      "target" -> translateTo
    )

    val request: WSRequest = ws.url(url)
      .addHttpHeaders("Accept" -> "application/json")
      .addHttpHeaders("Content-Type" -> "application/json")

    val futureResponse: Future[String] = request
      .withAuth(username, password, WSAuthScheme.BASIC)
      .post(data)
      .map {
        response => (response.json \ "translations" \\ "translation").map(_.as[String]).head
      }

    val response = Await.result(futureResponse,Duration.apply(1,TimeUnit.SECONDS))
    response
  }
}
