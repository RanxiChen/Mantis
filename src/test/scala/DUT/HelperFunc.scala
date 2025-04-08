package core.pipelined
import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import core.Colors._
//import chiseltest._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._

object dumpPU {
    def resetDut(dut:core.pipelined.TinySOC):Unit = {
        dut.reset.poke(true.B)
        dut.clock.step()
        dut.reset.poke(false.B)
        println("Run after reset")
    }
    def dumpPC(dut:core.pipelined.TinySOC,numeric_width:Int = 3):String = {
        "[" + s"%0${numeric_width}x".format(dut.io.mcycle.peek().litValue) + "]"
    } 
}

class NewSoc extends AnyFreeSpec with Matchers {
    "test soc" in {
        val max_cycle=25
        simulate(new TinySOC(1,"conf/rom.hex")(true)){dut =>
            dumpPU.resetDut(dut)
            for(cnt <- 0 to max_cycle){
                println(dumpPU.dumpPC(dut))
                dut.clock.step()
            }            
        }
    }
}