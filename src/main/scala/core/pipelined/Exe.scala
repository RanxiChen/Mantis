package core.pipelined

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._

class ExeModule extends Module{
    val io = IO(new Bundle{
        val in = Input(new PassuInstBundle)
        val out = Output(new ExeMemBundle)
        val taken = Output(Bool())
        val pc_sel = Output(core.Signal.PC_4.cloneType)
    })
    val alu = Module(new core.ALU.ALU)
    alu.io.src1 := io.in.src1
    alu.io.src2 := io.in.src2
    alu.io.op := io.in.alu_op
    io.out.alu_res := alu.io.out
    val bru = Module(new core.BrExe.BrU)
    bru.io.rs1 := io.in.rs1_reg
    bru.io.rs2 := io.in.rs2_reg
    bru.io.BrOp := io.in.bru_op
    io.taken := bru.io.taken
    io.out.wb_sel := io.in.wb_sel
    io.out.wb_en := io.in.wb_en
    io.out.rd_addr := io.in.rd_addr
    io.out.pc_4 := io.in.pc_4
    io.out.imm_u := io.in.imm_u
    io.out.mem_width := io.in.mem_width
    io.out.mem_sig := io.in.mem_sig
    io.out.mem_we := io.in.mem_we
    io.out.rs2_data := io.in.rs2_data
    io.out.notbubble := io.in.notbubble
    io.pc_sel := io.in.pc_sel
    //printf("[EXE] op1:0x%x,op2:0x%x,op:%x,alu_res:0x%x(bru op = %x,taken = %x)\t",io.in.src1,io.in.src2,io.in.alu_op,io.out.alu_res,io.in.bru_op,io.taken)
}
class ExeModuleProbeIO extends Bundle{
        val src1 = Output(UInt(64.W))
        val src2 = Output(UInt(64.W))
        val alu_op = Output(UInt(4.W))
        val alu_res = Output(UInt(64.W))
        val pc = Output(UInt(64.W))
        val notbubble = Output(Bool())
        val pc_sel = Output(core.Signal.PC_4.cloneType)
}

class ExeModuleWithProbe extends ExeModule {
    val probe = IO(new ExeModuleProbeIO)
    probe.src1 := io.in.src1
    probe.src2 := io.in.src2
    probe.alu_op := io.in.alu_op
    probe.alu_res := io.out.alu_res
    probe.pc := io.in.pc_4 -4.U
    probe.notbubble := io.in.notbubble
    probe.pc_sel := io.in.pc_sel
}
object ExeModule {
    def apply(probe: Boolean=false): ExeModule = {
        if (probe) {
            val exeModule = Module(new ExeModuleWithProbe)
            exeModule
        } else {
            val exeModule = Module(new ExeModule)
            exeModule
        }
    }
}