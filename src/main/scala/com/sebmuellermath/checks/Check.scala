package com.sebmuellermath.checks

import com.sebmuellermath.checks.Check.ExpectedResults
import com.sebmuellermath.domain._

/**
  * This is a check, it pairs a Request with a Response.
  */
case class Check(expected: ExpectedResults) {

  def check(request: Request, response: Response): CheckResult = {
    val reqVal = request.value
    expected.get(reqVal).fold(
      Pass(request, response))(
      vals =>
        // put more here
        Pass(request, response)
    )
  }
}

object Check {
  type ExpectedResults = Map[Int, List[Int]]

  val preloadedResults = Map(
    5 -> List(0,1,1,2,3,5),
    10 -> List(0,1,1,2,3,5,8)
  )

  def checkWithPreloadedResults(req: Request, res: Response): CheckResult = {
    Check(preloadedResults).check(req, res)
  }
}

