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
