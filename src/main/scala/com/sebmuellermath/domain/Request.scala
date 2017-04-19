package com.sebmuellermath.domain

import java.net.URL
import argonaut._
import Argonaut._
import org.http4s.EntityEncoder
import org.http4s.argonaut._

case class JobId(value: String)

object JobId {
  implicit def CodecJobId: CodecJson[JobId] = CodecJson(
    a => a.value.asJson,
    a => a.as[String].map(JobId.apply)
  )
}

case class Request(id: JobId, value: Int, webhookUrl: String)

object Request {
  implicit def CodecRequest: CodecJson[Request] =
    casecodec3(Request.apply, Request.unapply)("id", "value", "webhookUrl")
}