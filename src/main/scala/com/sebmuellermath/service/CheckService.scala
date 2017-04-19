package com.sebmuellermath.service

import com.sebmuellermath.domain._
import com.sebmuellermath.checks.Check
import scalaz._
import Scalaz._
import scalaz.concurrent.Task

case class CheckService() {
// combines the results.

  var store: Map[JobId, Request] = Map.empty // make this concurrent hashmap

  def submitRequest(request: DispatchedRequest): Task[Unit] = Task {
    request match {
      case SuccessfullyDispatchedRequest(req) => {
        store = store + (req.id -> req) // not thread safe
      }
      case FailedDispatchRequest(e) => {
        println(s"error dispatching request: $e")
      }
    }
  }

  def submitResponse(response: Response): Task[Unit] = {
    store.get(response.id).fold(
      Task(println("response with no matching request")))(
      request => runCheck(request, response).flatMap(reportResults)
    )
  }

  def runCheck(request: Request, response: Response): Task[CheckResult] = Task {
    Check.run(request, response)
  }

  def reportResults(result: CheckResult): Task[Unit] = ???
}
