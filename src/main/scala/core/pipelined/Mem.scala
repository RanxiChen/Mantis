package core.pipelined

import chisel3._
import chisel3.util._
import core.Signal._



class MemModule extends Module{
    val io = IO(new Bundle{
        val in = Input(new ExeMemBundle)
        val out = Output(new MemWritebackBundle)
        val getdata = new FetchDataIO
    })
    io.out.wb_sel := io.in.wb_sel
    io.out.wb_en := io.in.wb_en
    io.out.rd_addr := io.in.rd_addr
    io.out.pc_4 := io.in.pc_4
    io.out.alu_res := io.in.alu_res
    io.out.imm_u := io.in.imm_u
    io.getdata.addr := io.in.alu_res
    io.getdata.WE := io.in.mem_we
    io.getdata.bfwd := io.in.mem_width
    io.getdata.sig := io.in.mem_sig
    io.getdata.wdata := io.in.rs2_data
    io.out.mem_res := io.getdata.rdata
    io.out.notbubble := io.in.notbubble
    /*when(io.in.mem_we){
        //Write
        printf("[MEM] Write 0x%x to addr:0x%x,with bfwd:%x,no width\t",io.getdata.wdata,io.getdata.addr,io.getdata.bfwd)
    }.otherwise {
        printf("[MEM]  Read 0x%x from addr:0x%x,with sig:%x,bfwd:%x\t",io.getdata.rdata,io.getdata.addr,io.getdata.sig,io.getdata.bfwd)
    }*/
}

class MemModuleProbeIO extends Bundle {
        val addr = Output(UInt(64.W))
        val WE = Output(Bool())
        val bfwd = Output(UInt(3.W))
        val sig = Output(Bool())
        val wdata = Output(UInt(64.W))
        val rdata = Output(UInt(64.W))
}
class MemModuleWithProbe extends MemModule {
    val probe = IO(new MemModuleProbeIO)
    probe.addr := io.getdata.addr
    probe.WE := io.getdata.WE
    probe.bfwd := io.getdata.bfwd
    probe.sig := io.getdata.sig
    probe.wdata := io.getdata.wdata
    probe.rdata := io.out.mem_res
}
object MemModule {
    def apply(probe: Boolean=false): MemModule = {
        if (probe) {
            val memModule = Module(new MemModuleWithProbe)
            memModule
        } else {
            val memModule = Module(new MemModule)
            memModule
        }
    }
}