package core

import chisel3._
import chisel3.util._

class CtrlIO extends Bundle {
  val inst = Input(UInt(32.W))
  val pc_sel = Output(UInt(2.W))
  val imm_sel = Output(UInt(3.W))
  val alu_src1 = Output(UInt(1.W))
  val alu_src2 = Output(UInt(2.W))
  val alu_op = Output(UInt(4.W))
  val bru_op = Output(UInt(3.W))
  val MemWE = Output(Bool())
  val MemWidth = Output(UInt(3.W))
  val MemSig = Output(Bool())
  val RegWE = Output(Bool())
  val RfSel = Output(UInt(2.W)) 
}

