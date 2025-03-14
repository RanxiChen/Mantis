package core

import chisel3._
import chisel3.experimental.BundleLiterals._
//import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
//import chisel3.testers.TesterDriver.VerilatorBackend
import chiseltest._
/*
class RV64IPUSpec1 extends AnyFreeSpec with Matchers {
    "addi x1 x0 1 " in {
        val hex_raw = tool.HexProcess.generateUsefulHex()
        hex_raw(0) = f"${tool.CC.CC("addi",1,imm=1)}%08x"
        os.write.over(
            os.pwd/"misc/Mem/rom.hex",
            hex_raw.mkString("\n")
        )
        simulate(new single_cycleRV64Ip_PU(1,debug=true) ){dut =>
            //reset
            dut.reset.poke(true.B)
            dut.clock.step()
            dut.reset.poke(false.B)
            println("Before run after reset")
            println(s"x1 = ${dut.io.port.get.rfPort.content(1).peek().litValue.toString(16)}")
            dut.clock.step()
            println("After 1 cycle")
            println(s"x1 = ${dut.io.port.get.rfPort.content(1).peek().litValue.toString(16)}")
        }
    }
}
*/
/*
class RV64Itest03_PUSpec extends AnyFreeSpec with Matchers {
  "test file test01" in {
    simulate(new RV64_PU(16,"unittest/RV64/test01.hex",true,"unittest/RV64/test01.ini")){dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      println("Run after reset")
      for(index <- 0 until 10){
        dut.clock.step()
      }            
    }
  }
}
*/
class RV64PUtest01_01Spec extends AnyFreeSpec with Matchers with ChiselScalatestTester {
  "test file test01"  in {
    test(new RV64_PU(16,"unittest/RV64/test01.hex",true,"unittest/RV64/test01.ini")).withAnnotations(Seq(VerilatorBackendAnnotation)){dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      println("Run after reset")
      var cyclecnt=0
      println(dut.io.port.get.dpPort.pc.peekInt().toString(16))
      dut.clock.step()
      //First cycle
      cyclecnt += 1
      println(s"Cycle ${cyclecnt}")
      println(dut.io.port.get.dpPort.pc.peekInt().toString(16))   
      dut.io.port.get.dpPort.rf.addr.poke(2.U)
      println(dut.io.port.get.dpPort.rf.data.peekInt().toString(16))     
    }
  }
}
