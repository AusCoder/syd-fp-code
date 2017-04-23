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


/**
  * implicit that is used to convert an argonaut EncodeJson[A]
  * to an http4s EntityEncoder[A]
  */
object EncodeJsonToEntityEncoder {
  implicit def EntityEncoderFromEncodeJson[A](implicit jsonEncoder: EncodeJson[A]): EntityEncoder[A] =
    jsonEncoderOf(jsonEncoder)
}

/**
  * Dispatches Requests and submits them to a check service
  */
case class RequestDispatcher(baseUrlStr: String, httpClient: Client, checkService: CheckService) {
  import EncodeJsonToEntityEncoder._ // import the function to convert EncodeJson to EntityEncoder
  import Request._ // import the implicit CodecRequest

  /* parse the uri string, wrapping errors into a Task that will fail*/
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

    /* catch errors and wrap into our ADT */
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

/**
  * Factory method that provides SimpleHttp1Client
  */
object RequestDispatcher {
  def getSimpleDispatcher(baseUrlStr: String, checkService: CheckService) =
    RequestDispatcher(baseUrlStr, SimpleHttp1Client(), checkService)
}
