package com.sebmuellermath.service

import argonaut.EncodeJson
import com.sebmuellermath.domain._
import org.http4s.argonaut.jsonEncoderOf
import org.http4s.client.blaze.SimpleHttp1Client
import org.http4s.{EntityEncoder, Method, Uri, Request => HttpRequest}
import org.http4s.client.Client

import scalaz._
import Scalaz._
import scalaz.concurrent.Task


object EncodeJsonToEntityEncoder {
  implicit def EntityEncoderFromEncodeJson[A](implicit jsonEncoder: EncodeJson[A]): EntityEncoder[A] =
    jsonEncoderOf(jsonEncoder)
}

case class RequestDispatcher(baseUrlStr: String, httpClient: Client, checkService: CheckService) {
  import EncodeJsonToEntityEncoder._ // import the function to convert EncodeJson to EntityEncoder
  import Request._ // import the implicit CodecRequest

  // parse the uri string, wrapping any errors into the fail side of a Task.
  // an alternative is to use EitherT[Task, Throwable, _]
  val baseUrl: Task[Uri] =
    Uri.fromString(baseUrlStr + "/compute-fibonaccis").fold(
      failure => Task.fail(new Exception(failure.details)),
      uri => Task.delay(uri)
    )

  def dispatch(request: Request): Task[DispatchedRequest] = {
    val task = for {
      url         <- baseUrl
      req         = HttpRequest(Method.POST, url)
      reqWithBody <- req.withBody(request)
      _           <- httpClient.expect[String](reqWithBody)
    } yield SuccessfulRequest(request)

    // this catches any errors that occurred in the task
    // and wraps them into our ADT
    task.attempt.map(_.fold(
      e => FailedRequest(e),
      identity
    ))
  }

  def dispatchAndSubmit(request: Request): Task[DispatchedRequest] = {
    for {
      disp <- dispatch(request)
      _    <- checkService.submitDispatchedRequest(disp)
    } yield disp
  }
}

object RequestDispatcher {
  def getSimpleDispatcher(baseUrlStr: String, checkService: CheckService) =
    RequestDispatcher(baseUrlStr, SimpleHttp1Client(), checkService)
}
