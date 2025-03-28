package core.pipelined

import chisel3._
import chisel3.util._
import core.Signal._
class WriteBackModule extends Module {
    val io = IO(new Bundle{
        val in = Input(new MemWritebackBundle)
        val out = Output(new RegFileWritePort)
    })
    io.out.rd_addr := io.in.rd_addr
    io.out.rd_data := MuxCase(0.U,
    IndexedSeq(
        (io.in.wb_sel === DATA_ALU) -> io.in.alu_res,
        (io.in.wb_sel === DATA_MEM) -> io.in.mem_res,
        (io.in.wb_sel === DATA_PC4) -> io.in.pc_4,
        (io.in.wb_sel === DATA_IMM) -> io.in.imm_u
    )
    )
    io.out.WriteEnable := io.in.wb_en
    printf("[WB] write 0x%x to Reg:0x%x, with en:%x\n",io.out.rd_data,io.out.rd_addr,io.out.WriteEnable)
}