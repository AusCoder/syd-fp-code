package com.sebmuellermath.checks

import com.sebmuellermath.domain._

/**
  * This is a check, it pairs a Request with a Response.
  */
object Check {
  val knownResults = Map(
    5 -> List(0,1,1,2,3,5),
    10 -> List(0,1,1,2,3,5,8)
  )


  def run(request: Request, response: Response): CheckResult = {
    val reqVal = request.value
    knownResults.get(reqVal).fold(
      Pass(request, response))(
      vals =>
        // put more here
        Pass(request, response)
    )
  }
}
