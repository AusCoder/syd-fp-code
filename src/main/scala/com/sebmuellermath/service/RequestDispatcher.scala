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
  implicit def EntityEncodeRequest[A](implicit jsonEncoder: EncodeJson[A]): EntityEncoder[A] =
    jsonEncoderOf(jsonEncoder)
}

case class RequestDispatcher(baseUrlStr: String, httpClient: Client) {
  import EncodeJsonToEntityEncoder._ // import the function to convert EncodeJson to EntityEncoder
  import Request._ // import the implicit CodecRequest

  val baseUrl: EitherT[Task, Throwable, Uri] =
    EitherT.fromDisjunction[Task](Uri.fromString(baseUrlStr + "/compute-fibonaccis"))
      .leftMap(failure => new Exception(s"Failed to parse url: ${failure.sanitized}"))

  def dispatch(request: Request): EitherT[Task, Throwable, String] = {
    for {
      url <- baseUrl
      req = HttpRequest(Method.POST, url)
      reqWithBody <- EitherT.eitherT(req.withBody(request).attempt)
      resp <- EitherT.eitherT(httpClient.expect[String](reqWithBody).attempt)
    } yield resp
  }
}

object RequestDispatcher {
  def getSimpleDispatcher(baseUrlStr: String) =
    RequestDispatcher(baseUrlStr, SimpleHttp1Client())
}
