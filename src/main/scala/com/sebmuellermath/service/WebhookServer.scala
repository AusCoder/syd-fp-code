package com.sebmuellermath.service

import org.http4s.HttpService
import org.http4s.dsl._
import org.http4s.server.Router
import org.http4s.server.syntax._
import org.http4s.argonaut.jsonOf
import org.http4s.EntityDecoder
import com.sebmuellermath.domain.Response
import argonaut._
import Argonaut._

object DecodeJsonToEntityDecoder {
  implicit def EntityDecoderFromDecodeJson[A](implicit jsonDecoder: DecodeJson[A]): EntityDecoder[A] = {
    jsonOf(jsonDecoder)
  }
}

case class WebhookServer(checkService: CheckService) {
  import DecodeJsonToEntityDecoder._
  import Response._

  val service: HttpService = HttpService {

    case req @ POST -> Root / "results" => req.decode[Response] { response =>
      for {
        _ <- checkService.submitResponse(response)
        x <- Ok()
      } yield x
    }

    case GET -> Root / "test" => {
      Ok("this is test string")
    }
  }
}
