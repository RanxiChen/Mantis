package core.MDU

import chisel3._
import chisel3.util._

object MDUOp {
    val OP_XXX = 0.U(3.W)
    val OP_MUL = 0.U(3.W)
    val OP_MULH = 1.U(3.W)
    val OP_MULHSU = 2.U(3.W)
    val OP_MULHU = 3.U(3.W)
    val OP_DIV = 4.U(3.W)
    val OP_DIVU = 5.U(3.W)
    val OP_REM = 6.U(3.W)
    val OP_REMU = 7.U(3.W)
}
class MDU extends Module {
    val io = IO(new Bundle{
        val src1 = Input(UInt(64.W))
        val src2 = Input(UInt(64.W))
        val out = Output(UInt(64.W))
        val op = Input(MDUOp.OP_XXX.cloneType)
    })
    io.out := 0.U
    switch(io.op){
        is(MDUOp.OP_MUL) { io.out := (io.src1 * io.src2)(63,0) }
    }
}
