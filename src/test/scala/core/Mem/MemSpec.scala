package core.Mem

import chisel3._
import chisel3.util._

import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class MemPorbePortReadSpec extends AnyFreeSpec with Matchers {
  "test read by ProbePort" in {
    simulate(new MainMem(1)()(true)) { dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.io.IFPort.addr.poke(0.U)
      dut.io.MemPort.addr.poke(0.U)
      dut.io.MemPort.WE.poke(false.B)
      //dut.io.ProbePort.get.addr.poke(1.U)
      //println(dut.io.ProbePort.get.data.peek().litValue)
      //dut.io.ProbePort.get.addr.poke(1023.U)
      //println(dut.io.ProbePort.get.data.peek().litValue)
      for( i <- 0 until 256){
        dut.io.ProbePort.get.addr.poke(i.U)
        dut.io.ProbePort.get.data.expect(i.U)
        //println(s"This ${i}-th location is ${dut.io.ProbePort.get.data.peek().litValue}")
      }
    }
  }
}
class MemIFPortReadSpec extends AnyFreeSpec with Matchers {
  "test read by IFPort" in {
    simulate(new MainMem(1)()(true)) { dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      //32 bits read
      val HexfilePath = os.pwd/"misc/Mem/rom.hex"
      val raw_hex_Seq = os.read.lines(HexfilePath)
      var ifaddr=0
      for(line <- raw_hex_Seq){
        dut.io.IFPort.addr.poke(ifaddr.U)
        //println(s"data:${BigInt(line,16)} at addr:${ifaddr} store ${dut.io.IFPort.inst.peek().litValue}")
        dut.io.IFPort.inst.expect(BigInt(line,16).U)
        ifaddr += 4
      }
  }
}
}

class MemMemPortReadSpec extends AnyFreeSpec with Matchers {
  "test read by MemPort" in {
    simulate(new MainMem(1)()(true)) { dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      //32 bits hex file
      val HexfilePath = os.pwd/"misc/Mem/rom.hex"
      dut.io.MemPort.WE.poke(false.B)
      //just byte
      dut.io.MemPort.bfwd.poke("b000".U)
      //no sig extends
      dut.io.MemPort.sig.poke(false.B)
      val testCase=List(0x1,0x5,0xf,0x10,0x15,0x1a,0x5f,0x7a,0xf2)
      for(testcase <- testCase){
        dut.io.MemPort.addr.poke(testcase.U)
        //println(s"data:${testcase} at addr:${testcase} store ${dut.io.MemPort.rdata.peek().litValue}")
        dut.io.MemPort.rdata.expect(testcase.U)
      }
      //half,still no sig extends
      dut.io.MemPort.bfwd.poke("b001".U)
      val halfresBar=List(0x0201,0x0605,0x100f,0x1110,0x1615,0x1b1a,0x605f,0x7b7a,0xf3f2)
      for(testcase <- (testCase zip halfresBar)){
        dut.io.MemPort.addr.poke(testcase._1.U)
        //println(s"using testcase:${testcase}, get data:${(dut.io.MemPort.rdata.peek().litValue)}")
        //dut.io.MemPort.addr.poke(testcase._1.U)
        dut.io.MemPort.rdata.expect(testcase._2.U)
      }
    }
  }
}
