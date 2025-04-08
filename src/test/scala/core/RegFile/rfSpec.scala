package core

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class rfSpec extends AnyFreeSpec with Matchers {
  "test init value from conf" in {
    os.write.over(os.pwd/"conf/reg.ini", "sp=0x3ffc\ngp=0x1800\n")
    simulate(new RegFileModule(true, "conf/reg.ini")) { dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      dut.io.probe.get.addr.poke(2.U)
      dut.io.probe.get.data.expect(0x3ffc.U)
      dut.io.probe.get.addr.poke(3.U)
      dut.io.probe.get.data.expect(0x1800.U)
  }
  }
}

class PipelinedRegFileSpec extends AnyFreeSpec with Matchers {
  "test read/write data at same time" in {
    simulate(new pipelined.PipelinedRegFileWithWatchPort){dut =>
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)

      dut.debug.addr.poke(2.U)
      dut.debug.data.expect(0.U)
      
      dut.io.writePort.rd_addr.poke(2.U)
      dut.io.writePort.rd_data.poke(0x3ffc.U)
      dut.io.writePort.WriteEnable.poke(true.B)
      dut.io.readPort.src1_addr.poke(2.U)
      dut.clock.step()

      dut.debug.addr.poke(2.U)
      dut.debug.data.expect(0x3ffc.U)
    }
  }
}