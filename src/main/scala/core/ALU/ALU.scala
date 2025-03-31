package core.ALU

import chisel3._
import chisel3.util._

import _root_.circt.stage.ChiselStage

object ALUOp {
  val OP_ADD = 0.U(4.W)
  val OP_SUB = 1.U(4.W)
  val OP_AND = 2.U(4.W)
  val OP_OR  = 3.U(4.W)
  val OP_XOR = 4.U(4.W)
  val OP_SLL = 5.U(4.W)
  val OP_SRL = 6.U(4.W)
  val OP_SRA = 7.U(4.W)
  val OP_SLT = 8.U(4.W)
  val OP_SLTU = 9.U(4.W)
  val OP_USED = 10.U(4.W)
  val OP_XXX = 0.U(4.W)
}


class ALU extends Module {
  val io = IO(new Bundle {
    val src1 = Input(UInt(64.W))
    val src2 = Input(UInt(64.W))
    val out = Output(UInt(64.W))
    val op = Input(UInt(4.W))
  })
  io.out := 0.U
  switch(io.op) {
    is(ALUOp.OP_ADD) { io.out := io.src1 + io.src2 }
    is(ALUOp.OP_SUB) { io.out := io.src1 - io.src2 }
    is(ALUOp.OP_AND) { io.out := io.src1 & io.src2 }
    is(ALUOp.OP_OR)  { io.out := io.src1 | io.src2 }
    is(ALUOp.OP_XOR) { io.out := io.src1 ^ io.src2 }
    is(ALUOp.OP_SLL) { io.out := io.src1 << io.src2(5,0) }
    is(ALUOp.OP_SRL) { io.out := io.src1 >> io.src2(5,0) }
    is(ALUOp.OP_SRA) { io.out := (io.src1.asSInt >> io.src2(5,0)).asUInt }
    is(ALUOp.OP_SLT) {io.out := (io.src1.asSInt < io.src2.asSInt)}
    is(ALUOp.OP_SLTU) {io.out := (io.src1 < io.src2)}
    is(ALUOp.OP_USED) {io.out := 0.U}
  }
}


object ALU_V extends App {
  ChiselStage.emitSystemVerilogFile(
    new ALU,
    Array("--target-dir", "build/ALU"),
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}

