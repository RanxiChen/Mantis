package core.MDU

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

object M_tool {
  def op(op:String):Int = {
    ???
  }
}

class MDUSpec extends AnyFreeSpec with Matchers {
  "MDU mul" in {
    simulate(new MDU){
        dut =>
            dut.io.src1.poke(2.U)
            dut.io.src2.poke(3.U)
            dut.io.op.poke(MDUOp.OP_MUL)
            dut.io.out.expect(6.U)
    }
  }
}