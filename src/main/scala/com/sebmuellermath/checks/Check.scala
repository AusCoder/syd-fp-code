package com.sebmuellermath.checks

import com.sebmuellermath.checks.Check.ExpectedResults
import com.sebmuellermath.domain._

/**
  * This is a check, it pairs a Request with a Response.
  */
case class Check(expected: ExpectedResults) {

  def runCheck(request: Request, response: Response): CheckResult = {
    val reqVal: Int = request.value
    val expectedResultsOpt: Option[List[Int]] = expected.get(reqVal)
    expectedResultsOpt.fold[CheckResult](
      Pass(request, response))(
      vals =>
        response match {
          case FailedComputation(_, _) => Fail(request, response, "received failed computaiton")
          case CompletedComputation(_, results) => {
            if (vals == results) Pass(request, response)
            else Fail(request, response, "values didn't match")
          }
        }
    )
  }
}

object Check {
  type ExpectedResults = Map[Int, List[Int]]

  val preloadedResults = Map(
    5 -> List(0,1,1,2,3,5),
    10 -> List(0,1,1,2,3,5,8)
  )

  def checkWithPreloadedResults: Check = {
    Check(preloadedResults)
  }
}

