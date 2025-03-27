package core.pipelined

import core.Mem.IFMemIO
import chisel3._
import chisel3.util._

class FetchInstIO extends Bundle{
    val addr = Input(UInt(64.W))
    val inst = Output(UInt(64.W))
}

class SimpleFetchModule extends Module{
    val io = IO(new Bundle{
        val pcIn = Input(new PassPCBundle)
        val getInst = Input(new FetchInstIO)
        val out = Output( new PassPCInstBundle)
    })
    io.getInst.addr := io.pcIn.pc
    io.out.pc := io.pcIn.pc
    io.out.inst := io.getInst.inst
    println("IF will just get inst from port")
    printf("[IF] PC: 0x%x, Inst: 0x%x\n", io.pcIn.pc, io.getInst.inst)
}