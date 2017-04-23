package com.sebmuellermath.domain

import argonaut._
import Argonaut._

sealed trait Response {
  val id: JobId
}
case class CompletedComputation(id: JobId, results: List[Int]) extends Response
case class FailedComputation(id: JobId, message: String) extends Response

object Response {
  def CodecCompletedComputation: CodecJson[CompletedComputation] =
    casecodec2(CompletedComputation.apply, CompletedComputation.unapply)("id", "results")

  def CodecFailedComputation: CodecJson[FailedComputation] =
    casecodec2(FailedComputation.apply, FailedComputation.unapply)("id", "message")

  implicit def ComputationResult: CodecJson[Response] = CodecJson(
    a => a.asJson,
    (CodecCompletedComputation.map(a => a: Response) ||| CodecFailedComputation.map(a => a: Response)).decode(_)
  )
}