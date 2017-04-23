package com.sebmuellermath.service

import com.sebmuellermath.domain._
import com.sebmuellermath.checks.Check
import scalaz._
import Scalaz._
import scalaz.concurrent.Task

/**
  * Class that combines DispatchedRequests and Results.
  */
case class CheckService() {

  var store: Map[JobId, Request] = Map.empty // make this concurrent hashmap

  def submitDispatchedRequest(dispatchedRequest: DispatchedRequest): Task[Unit] = Task {
    dispatchedRequest.fold(
      e => println(s"error dispatching request: $e"),
      req => store = store + (req.id -> req)
    )
  }

  def submitResponse(response: Response): Task[Unit] = {
    store.get(response.id).fold(
      Task(println("response with no matching request")))(
      request => runCheck(request, response).flatMap(reportResults)
    )
  }

  def runCheck(request: Request, response: Response): Task[CheckResult] = Task {
    Check.checkWithPreloadedResults(request, response)
  }

  def reportResults(result: CheckResult): Task[Unit] = Task {
    println(result)
  }
}
