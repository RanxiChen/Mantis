package core.PU

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class ExtInstNoMemPUSpec extends AnyFreeSpec with Matchers {
    "Just add 1 to x1" in {
        simulate(new ExtInstNoMemPU){ dut =>
           val inst=BigInt("00110093",16) //x1 = x2 +1
           dut.io.inst.poke(inst.U)
           dut.clock.step()
           dut.io.regValue(1).expect(1.U)           
        }
    }
    "x1 = x2+1,x3=x4+1,x5=x3+x1" in {
        simulate(new ExtInstNoMemPU){ dut =>
           val inst=BigInt("00110093",16) //x1 = x2 +1
           dut.io.inst.poke(inst.U)
           dut.clock.step()
           dut.io.regValue(1).expect(1.U)           
           dut.io.inst.poke(BigInt("00120193",16))
           dut.clock.step()
           dut.io.regValue(3).expect(1.U)
           dut.io.inst.poke(BigInt("1182B3",16).U)
           dut.clock.step()
           dut.io.regValue(5).expect(2.U)
        }
    }
}