package core

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage

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
