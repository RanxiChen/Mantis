package core.InstructionMem

import chisel3._

class InstrMem extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(32.W))
    val instr = Output(UInt(32.W))
  })
  val instr_mem = Mem(1024, UInt(32.W))
  io.instr := instr_mem(io.addr)
}
