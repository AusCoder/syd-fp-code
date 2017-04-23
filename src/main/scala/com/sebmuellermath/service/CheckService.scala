package com.sebmuellermath.service

import com.sebmuellermath.domain._
import com.sebmuellermath.checks.Check
import scalaz._
import Scalaz._
import scalaz.concurrent.Task

/**
  * Class that combines DispatchedRequests and Results.
  */
case class CheckService(check: Check) {

  var store: Map[JobId, Request] = Map.empty // make this concurrent hashmap

  def submitDispatchedRequest(dispatchedRequest: DispatchedRequest): Task[Unit] = Task {
    dispatchedRequest.fold(
      e => println(s"error dispatching request: $e"),
      req => store = store + (req.id -> req)
    )
  }

  def liftedRunCheck(request: Request, response: Response): Task[CheckResult] = Task {
    check.runCheck(request, response)
  }

  def submitResponse(response: Response): Task[Unit] = {
    store.get(response.id).fold(
      Task(println("response with no matching request")))(
      request => liftedRunCheck(request, response).flatMap(reportResults)
    )
  }

  def reportResults(result: CheckResult): Task[Unit] = Task {
    println(result)
  }
}
