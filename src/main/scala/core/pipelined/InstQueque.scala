package core.pipelined

import chisel3._
import chisel3.util._
import core.pipelined
import chisel3.experimental.BundleLiterals._
import core.Instructions
class InstQuequeBuilder extends Module {
    val io = IO(new Bundle{
        val in = Input(new PassPCInstBundle)
        val out = Output(new PassPCInstBundle)
        val en = Input(Bool())
        val clr = Input(Bool())
    })
}

class SingleInstQueue extends InstQuequeBuilder {
    val initialValue = (new PassPCInstBundle).Lit(
      _.inst -> Instructions.NOP,
      _.pc -> 0.U,
      _.pc_4 -> 0.U,
      _.notbubble -> false.B,
    )
    val instQueque = RegInit(initialValue)
    when(io.clr){
      instQueque := initialValue
    }.elsewhen(io.en){
      instQueque := io.in
    }
    io.out := instQueque
}
class InstQuequeWithProbe extends SingleInstQueue{
    val probe = IO(new PassPCInstBundle)
    probe.pc := instQueque.pc
    probe.pc_4 := instQueque.pc_4
    probe.inst := instQueque.inst
    probe.notbubble := instQueque.notbubble
    //printf("[IF] from PC = 0x%x get Inst: 0x%x\t", io.out.pc, io.out.inst)
}
