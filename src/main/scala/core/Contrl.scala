package core

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
class CtrlIO extends Bundle {
  val inst = Input(UInt(32.W))
  val pc_sel = Output(UInt(2.W))
  val imm_sel = Output(UInt(3.W))
  val alu_src1 = Output(UInt(1.W))
  val alu_src2 = Output(UInt(2.W))
  val alu_op = Output(UInt(4.W))
  val bru_op = Output(UInt(3.W))
  val mem_we = Output(Bool())
  val mem_width = Output(UInt(3.W))
  val mem_sig = Output(Bool())
  val wb_en = Output(Bool())
  val wb_sel = Output(UInt(3.W)) 
}

class CtrlU_RV64I extends Module{
  val io = IO(new CtrlIO)
  import core.Signal._
  import core.ALU.ALUOp._ 
  import core.BrExe.BrOp._
  import core.IDMap._
  import Instructions._
  val ctrlSignals = ListLookup(io.inst, defaultCtrl, instrMap)
  io.pc_sel := ctrlSignals(0)
  io.alu_src1 := ctrlSignals(1)
  io.alu_src2 := ctrlSignals(2)
  io.imm_sel := ctrlSignals(3)
  io.alu_op := ctrlSignals(4)
  io.bru_op := ctrlSignals(5)
  io.mem_width := ctrlSignals(6)
  io.mem_sig := ctrlSignals(7)
  io.mem_we := ctrlSignals(8)
  io.wb_sel := ctrlSignals(9)
  io.wb_en := ctrlSignals(10)
}


object CtrlU_V extends App {
  ChiselStage.emitSystemVerilogFile(
    new CtrlU_RV64I,
    Array("--target-dir", "build/CtrlU"),
    firtoolOpts = Array("-disable-all-randomization","-strip-debug-info")
  )
}

object CtrlUInfo extends  App {
  val inst = args(0)
  println("inst: " + inst)
  val raw_inst = BigInt(inst.substring(2),16)
    simulate(new CtrlU_RV64I) { dut =>
      dut.io.inst.poke(raw_inst.U)
      println("pc_sel:" + dut.io.pc_sel.peek().litValue)
      println("alu_src1:" + dut.io.alu_src1.peek().litValue)
      println("alu_src2:" + dut.io.alu_src2.peek().litValue)
      println("imm_sel:" + dut.io.imm_sel.peek().litValue)
      println("alu_op:" + dut.io.alu_op.peek().litValue)
      println("bru_op:" + dut.io.bru_op.peek().litValue)
      println("mem_width:" + dut.io.mem_width.peek().litValue)
      println("mem_sig:" + dut.io.mem_sig.peek().litValue)
      println("mem_we:" + dut.io.mem_we.peek().litValue)
      println("wb_sel:" + dut.io.wb_sel.peek().litValue)
      println("wb_en:" + dut.io.wb_en.peek().litValue)
    }
}
