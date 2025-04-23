package core.pipelined

import core.Mem.IFMemIO
import chisel3._
import chisel3.util._

class FetchInstIO extends Bundle{
    val addr = Output(UInt(64.W))
    val inst = Input(UInt(64.W))
}

trait FetchModuleProbe {
    val probe = IO(Output(new PassPCInstBundle))
}

class SimpleFetchModule extends Module{
    val io = IO(new Bundle{
        val pcIn = Input(new PassPCBundle)
        val getInst = new FetchInstIO
        val out = Output( new PassPCInstBundle)
    })
    io.out.pc :=io.pcIn.pc
    io.out.pc_4 := io.pcIn.pc_4
    io.getInst.addr := io.pcIn.pc
    io.out.inst := io.getInst.inst
    io.out.notbubble := true.B
    println("IF will just get single inst from port")
    //printf("[IF] from PC = 0x%x get Inst: 0x%x\t", io.pcIn.pc, io.getInst.inst)
}

class SimpleFetchWithProbeModule extends SimpleFetchModule with FetchModuleProbe {
    println("IF stage will just get single inst from port")
    println("IF Module with probe")
    probe.pc := io.out.pc
    probe.pc_4 := io.out.pc_4
    probe.inst := io.out.inst
    probe.notbubble := io.out.notbubble
}