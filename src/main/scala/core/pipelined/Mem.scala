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
    if(io.in.mem_we.litToBoolean){
        //Write
        printf("[MEM] Write 0x%x to addr:0x%x,with bfwd:%x\n",io.getdata.wdata,io.getdata.addr,io.getdata.bfwd)
    }else {
        printf("[MEM] Read 0x%x from addr:0x%x,with sig:%x,bfwd:%x\n",io.getdata.rdata,io.getdata.addr,io.getdata.sig,io.getdata.bfwd)
    }
}