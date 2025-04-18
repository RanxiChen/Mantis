package core

import chisel3._
import chisel3.util._


class ImmGen_RV64I extends Module{
  val io = IO(new Bundle{
    val inst = Input(UInt(64.W))
    val imm = Output(UInt(64.W))
    val sel = Input(UInt(3.W))
  })
  
  io.imm := 0.U
  val immi = Fill(64-11,io.inst(31)) ## io.inst(30,20)
  val imms = Fill(64-11,io.inst(31)) ## io.inst(30,25) ## io.inst(11,7)
  val immb = Fill(64-12,io.inst(31)) ## io.inst(7) ## io.inst(30,25) ## io.inst(11,8) ## false.B
  val immj = Fill(64-20,io.inst(31)) ## io.inst(19,12) ## io.inst(20) ## io.inst(30,21) ## false.B
  val immu = Fill(64-32,io.inst(31)) ## io.inst(31,12) ## Fill(12,false.B)

  switch(io.sel){
    is(Signal.IMM_I){
      io.imm := immi
    }
    is(Signal.IMM_S){
      io.imm := imms
    }
    is(Signal.IMM_B){
      io.imm := immb
    }
    is(Signal.IMM_U){
      io.imm := immu
    }
    is(Signal.IMM_J){
      io.imm := immj
    }
  }
}
