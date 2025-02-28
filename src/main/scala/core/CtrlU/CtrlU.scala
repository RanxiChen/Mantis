package core.CtrlU

import chisel3._
import core.ALU.ALUOp._
/**
 * Control Unit for RVCORE
 */
class CtrlU extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(64.W))
    //OutPut
    val alu_op = Output(UInt(4.W))
    val regoimm = Output(Bool())
  })

  val op7 = Wire(UInt(7.W))
  op7 := io.inst(6,0)
  val func3=Wire(UInt(3.W))
  func3 := io.inst(14,12)
  io.alu_op := OP_USED

  io.regoimm := ( op7 === "b0110011".U) //R-type

  when(op7 === "b0010011".U){
    when(func3 === "b000".U ){
      io.alu_op := OP_ADD
    }.elsewhen(func3 === "b100".U ){
      io.alu_op := OP_XOR
    }.elsewhen(func3 === "b110".U){
      io.alu_op := OP_OR
    }.elsewhen(func3 ==="b111".U){
      io.alu_op := OP_AND
    }.elsewhen(func3 === "b001".U ){
      io.alu_op := OP_SLL
    }.elsewhen(func3 === "b101".U){
      io.alu_op := Mux(io.inst(30),OP_SRA,OP_SRL)
    }.elsewhen(func3 ==="b010".U){
      io.alu_op := OP_SLT
    }.elsewhen(func3 === "b011".U){
      io.alu_op := OP_SLTU
    }.otherwise{
      io.alu_op := OP_USED
    }
  }.elsewhen(op7 === "b0110011".U){
    when(func3 === "b000".U ){
      io.alu_op := Mux(io.inst(30),OP_SUB,OP_ADD)
    }.elsewhen(func3 === "b100".U ){
      io.alu_op := OP_XOR
    }.elsewhen(func3 ==="b110".U){
      io.alu_op := OP_OR
    }.elsewhen(func3 ==="b111".U){
      io.alu_op := OP_AND
    }.elsewhen(func3 === "b001".U ){
      io.alu_op := OP_SLL
    }.elsewhen(func3 === "b101".U){
      io.alu_op := Mux(io.inst(30),OP_SRA,OP_SRL)
    }.elsewhen(func3 === "b010".U ){
      io.alu_op := OP_SLT
    }.elsewhen(func3 === "b011".U ){
      io.alu_op := OP_SLTU
    }.otherwise{
      io.alu_op := OP_USED
    }

    
  }


}