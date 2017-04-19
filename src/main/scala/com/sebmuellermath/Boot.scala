package com.sebmuellermath

import java.net.URL
import com.sebmuellermath.service.RequestDispatcher
import com.sebmuellermath.domain.{JobId, Request}

object Boot {
  def main(args: Array[String]): Unit = {
    val webhookUrl = "http://localhost:8080/results"
    val sampleRequest = Request(JobId("1"), 50, webhookUrl)

    val dispatcher = RequestDispatcher.getSimpleDispatcher("http://localhost:5000")
    val task = dispatcher.dispatch(sampleRequest).run

    task.map(println).unsafePerformSync
  }
}
