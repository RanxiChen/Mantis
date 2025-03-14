package core

import chisel3._
import chisel3.experimental.BundleLiterals._

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import chiseltest._


class RV64PUtest01Spec extends AnyFreeSpec with Matchers with ChiselScalatestTester {
  "test file test01, some compute and jump"  in {
    test(new RV64_PU(16,"unittest/RV64/test01.hex",true,"unittest/RV64/test01.ini")).withAnnotations(Seq(VerilatorBackendAnnotation)){dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      println("Run after reset")
      val testSets = tool.PUPort.testSetsfromFile("unittest/RV64/test01.ref")
      val max_cycle=79
      for(cyclecnt <- 1 to max_cycle){
        println("cycel: " + cyclecnt + "----------------")
        dut.clock.step()
        if(testSets.contains(cyclecnt)){
          for((port,value) <- testSets(cyclecnt)){
            port match {
              case tool.PC => {dut.io.port.get.dpPort.pc.expect(value.U);println("[As expected] PC= 0x"+value.toString(16))}
              case tool.Reg(n) => {dut.io.port.get.dpPort.rf.addr.poke(n.U);dut.io.port.get.dpPort.rf.data.expect(value.U);println("[As expected] Reg"+n+"= 0x"+value.toString(16))}
              case tool.Mem(addr) => {println("There is some memeory operation")}
            }
          }
        }
      }
    }
  }
}
         