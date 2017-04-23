package com.sebmuellermath.domain

sealed trait DispatchedRequest {
  def fold[A](failed: Throwable => A, success: Request => A): A = this match {
    case FailedRequest(e)       => failed(e)
    case SuccessfulRequest(req) => success(req)
  }
}
case class FailedRequest(e: Throwable) extends DispatchedRequest
case class SuccessfulRequest(request: Request) extends DispatchedRequest

