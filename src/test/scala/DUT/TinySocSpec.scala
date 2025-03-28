package DUT

import chisel3._
import chisel3.experimental.BundleLiterals._

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import chiseltest._

class TinySocSpec extends AnyFreeSpec with Matchers with ChiselScalatestTester