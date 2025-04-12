package core.pipelined

import chisel3._
import chisel3.experimental.BundleLiterals._
import core.Signal._

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import core.Colors._
//import chiseltest._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
class TinySocSpec extends AnyFreeSpec with Matchers /*with ChiselScalatestTester */{
    //accept rom and run
    "test one Tiny Soc but 5 stage pipelined" in {
        simulate(new TinySOC(1,"conf/rom.hex")(true))/*.withAnnotations(Seq(VerilatorBackendAnnotation))*/{dut =>
            dumpPU.testSoc(dut,"",150,numeric_width = 3,after_inst_cnt=0)
        }
    }
}
class Peek extends AnyFreeSpec with Matchers {
    "peek how jump occur" in {
        simulate(new TinySOC(1,"conf/rom.hex")(true)){dut =>
            dumpPU.peek(dut,150,numeric_width = 3)
        }
    }
}