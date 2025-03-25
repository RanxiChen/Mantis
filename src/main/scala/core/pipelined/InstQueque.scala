package core.pipelined

import chisel3._
import chisel3.util._
import core.pipelined
import chisel3.experimental.BundleLiterals._
import core.Instructions
class InstQuequeBuilder extends Module {
    val io = IO(new Bundle{
        val in = Input(new PassPCInstIO)
        val out = Output(new PassPCInstIO)
    })
}

class SingleInstQueue extends InstQuequeBuilder {
    val instQueque = RegInit(
        (new PassPCInstIO).Lit(
            _.inst -> Instructions.NOP,
            _.pc -> 0.U
        )
    )
    instQueque <> io.in
    io.out <> instQueque
}