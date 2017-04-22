package com.sebmuellermath

import com.sebmuellermath.service.RequestDispatcher
import com.sebmuellermath.domain.{JobId, Request}
import com.sebmuellermath.service.WebhookServer
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.{Server, ServerApp}
import scalaz._
import Scalaz._
import scalaz.concurrent.Task


object Boot extends ServerApp {
  val webhookUrl = "http://localhost:37047/results"
  val sampleRequest = Request(JobId("1"), 50, webhookUrl)

  val dispatcher = RequestDispatcher.getSimpleDispatcher("http://localhost:5000")
  val task = dispatcher.dispatch(sampleRequest).run
  val printedTask = task.map(println)

  val many = for {
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
    _ <- printedTask
  } yield ()
  many.unsafePerformAsync(_ => ())

  def server(args: List[String]): Task[Server] = {
    BlazeBuilder
      .bindHttp(37047, "localhost")
      .mountService(WebhookServer.service, "/")
      .start
  }
}
