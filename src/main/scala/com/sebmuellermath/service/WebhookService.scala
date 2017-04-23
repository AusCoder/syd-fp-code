package com.sebmuellermath.service

import org.http4s.HttpService
import org.http4s.dsl._
import org.http4s.argonaut.jsonOf
import org.http4s.EntityDecoder
import org.http4s.server.syntax._
import com.sebmuellermath.domain.Response
import argonaut._
import Argonaut._

object DecodeJsonToEntityDecoder {
  implicit def EntityDecoderFromDecodeJson[A](implicit jsonDecoder: DecodeJson[A]): EntityDecoder[A] = {
    jsonOf(jsonDecoder)
  }
}

/**
  * HttpService that defines how we process webhook responses.
  * The web server isn't run here, it gets run in Boot.
  */
case class WebhookService(checkService: CheckService) {
  import DecodeJsonToEntityDecoder._
  import Response._

  val resultService: HttpService = HttpService {
    case req @ POST -> Root / "results" => req.decode[Response] { response =>
      for {
        _ <- checkService.submitResponse(response)
        x <- Ok()
      } yield x
    }
  }

  val testService: HttpService = HttpService {
    case GET -> Root / "test" => {
      Ok("this is test string")
    }
  }

  val service: HttpService = resultService orElse testService
}
