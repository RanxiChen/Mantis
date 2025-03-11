package core

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class RV64IPUSpec1 extends AnyFreeSpec with Matchers {
    "addi x1 x0 1 " in {
        val hex_raw = tool.HexProcess.generateUsefulHex()
        hex_raw(0) = f"${tool.CC.CC("ADDI","i",1,imm=1)}%08x"
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
class RV64IPUSpec2 extends AnyFreeSpec with Matchers {
  "a little complex asm" in {
    val hex_raw = tool.HexProcess.generateUsefulHex()
    //addi x1 x0 6
    hex_raw(0) = f"${tool.CC.CC("ADDI","i",1,imm=6)}%08x"
    //addi x2 x1 5
    hex_raw(1) = f"${tool.CC.CC("ADDI","i",rd=2,rs1=1,imm=5)}%08x"
    //sub x3 x2 x1
    hex_raw(2) = f"${tool.CC.CC("SUB" ,"r",rd=3,rs1=2,rs2=1)}%08x"
    //x1=6,x2=11,x3=5
    //load  64 to x4
    //lb x4 64(0)
    hex_raw(3) = f"${tool.CC.CC("LB"  ,"i",rd=4,rs1=0,imm=64)}%08x"
    //if 64 > x3, jump to 128
    //in 128, jump back to 0
    //jalr x10 0(x0)
    hex_raw(4) = f"${tool.CC.CC("BGE" ,"b",rs1=4,rs2=3,imm=128-4*4)}%08x"
    hex_raw(32) = f"${tool.CC.CC("JALR","i",rd=10,rs1=0,imm=0)}%08x"
    os.write.over(
      os.pwd/"misc/Mem/rom.hex",
      hex_raw.mkString("\n")
    )
    simulate(new single_cycleRV64Ip_PU(1,debug=true)){dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      for(i <- 0 until 10){
        dut.clock.step()
    }
  }
}
}
