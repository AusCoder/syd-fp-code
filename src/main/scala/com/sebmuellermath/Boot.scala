package com.sebmuellermath

import com.sebmuellermath.service.{CheckService, RequestDispatcher, WebhookServer}
import com.sebmuellermath.domain.{DispatchedRequest, JobId, Request}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.{Server, ServerApp}

import scalaz._
import Scalaz._
import scalaz.concurrent.Task


object Boot extends ServerApp {
  val webhookUrl = "http://localhost:37047/results"
  val sampleRequest = Request(JobId("1"), 50, webhookUrl)

  val checkService = CheckService()
  val dispatcher = RequestDispatcher.getSimpleDispatcher("http://localhost:5000", checkService)
  val webhookService = WebhookServer(checkService)

  val task = dispatcher.dispatchAndSubmit(sampleRequest)
  val many = for {
    _ <- task
    _ <- task
    _ <- task
    _ <- task
    _ <- task
    _ <- task
    _ <- task
    _ <- task
    _ <- task
    _ <- task
    _ <- task
    _ <- task
    _ <- task
  } yield ()
  many.unsafePerformAsync(_ => ())

  def server(args: List[String]): Task[Server] = {
    BlazeBuilder
      .bindHttp(37047, "localhost")
      .mountService(webhookService.service, "/")
      .start
  }
}
