package com.sebmuellermath.domain

case class Response(id: JobId, result: ComputationResult)

sealed trait ComputationResult
//case class InProgress(id: JobId) extends ComputationResult
case class CompletedComputation(id: JobId, primeFactors: List[Int], sumOfDigits: Int) extends ComputationResult
case class FailedComputation(id: JobId, message: String) extends ComputationResult
