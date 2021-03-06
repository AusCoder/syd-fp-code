package com.sebmuellermath.domain

sealed trait CheckResult

case class Pass(request: Request, response: Response) extends CheckResult
case class Fail(request: Request, response: Response, reason: String) extends CheckResult