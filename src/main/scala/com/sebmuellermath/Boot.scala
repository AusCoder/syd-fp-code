package com.sebmuellermath

import com.sebmuellermath.checks.Check
import com.sebmuellermath.service.{CheckService, RequestDispatcher, WebhookService}
import com.sebmuellermath.domain.{DispatchedRequest, JobId, Request}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.{Server, ServerApp}
import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import fs2._
import fs2.time.awakeEvery
import scala.concurrent.duration._
import fs2.interop.scalaz._


object Boot extends ServerApp {
  val webhookUrl = "http://localhost:37047/results"
  val sampleRequest = Request(JobId("1"), 10, webhookUrl)
  implicit val S = Scheduler.fromFixedDaemonPool(5) // required by awakeEvery

  val checks = Check.checkWithPreloadedResults
  val checkService = CheckService(checks)
  val dispatcher = RequestDispatcher.getSimpleDispatcher("http://localhost:5000", checkService)
  val webhookService = WebhookService(checkService)

  val requestStream: Stream[Task, DispatchedRequest] = {
    awakeEvery(1.second)
      .zip(Stream.range(0, Int.MaxValue))
      .map(_._2)
      .map(_.toString)
      .map(JobId.apply)
      .map(id => Request(id, 10, webhookUrl))
      .flatMap(req => Stream.eval(dispatcher.dispatchAndSubmit(req)))
  }

  requestStream.run.unsafePerformAsync(_ => ())

  val task = dispatcher.dispatchAndSubmit(sampleRequest)
  val many = for {
    _ <- task
    _ <- task
    _ <- task
    _ <- task
  } yield ()
//  many.unsafePerformAsync(_ => ())

  def server(args: List[String]): Task[Server] = {
    BlazeBuilder
      .bindHttp(37047, "localhost")
      .mountService(webhookService.service, "/")
      .start
  }
}
