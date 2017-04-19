package com.sebmuellermath.domain

sealed trait DispatchedRequest
// maybe make this a String
case class FailedDispatchRequest(e: Throwable) extends DispatchedRequest
case class SuccessfullyDispatchedRequest(request: Request) extends DispatchedRequest

