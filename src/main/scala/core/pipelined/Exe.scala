package core.pipelined

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._

class ExeModule extends Module{
    val io = IO(new Bundle{
        val in = Input(new PassuInstBundle)
        val out = Output(new ExeMemBundle)
        val taken = Output(Bool())
    })
    val alu = Module(new core.ALU.ALU)
    alu.io.src1 := io.in.src1
    alu.io.src2 := io.in.src2
    alu.io.op := io.in.alu_op
    io.out.alu_res := alu.io.out
    val bru = Module(new core.BrExe.BrU)
    bru.io.rs1 := io.in.src1
    bru.io.rs2 := io.in.src2
    bru.io.BrOp := io.in.bru_op
    io.taken := bru.io.taken
    io.out <> io.in
    printf("[EXE] op1:0x%x,op2:0x%x,op:%x,alu_res:0x%x(bru op = %x,taken = %x)\n",io.in.src1,io.in.src2,io.in.alu_op,io.out.alu_res,io.in.bru_op,io.taken)
}