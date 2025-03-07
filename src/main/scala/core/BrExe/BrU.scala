package core.BrExe

import chisel3._
import chisel3.util._

object BrOp {
  val Br_EQ = 0.U(3.W)
  val Br_NE = 1.U(3.W)
  val Br_LT = 2.U(3.W)
  val Br_LTU = 3.U(3.W)
  val Br_GE = 4.U(3.W)
  val Br_GEU = 5.U(3.W)
  val Br_USED = 6.U(3.W)
}

class BrU extends Module{
  val io = IO(new Bundle{
    val rs1 = Input(UInt(64.W))
    val rs2 = Input(UInt(64.W))
    val BrOp = Input(UInt(3.W))
    val taken = Output(Bool())
  })
  val eq = io.rs1 === io.rs2
  val ne = !eq
  val lt = io.rs1.asSInt < io.rs2.asSInt
  val ltu = io.rs1 < io.rs2
  val ge = io.rs1.asSInt >= io.rs2.asSInt
  val geu = io.rs1 >= io.rs2

  io.taken := false.B
  switch(io.BrOp){
    is(BrOp.Br_EQ){io.taken := eq}
    is(BrOp.Br_NE){io.taken := ne}
    is(BrOp.Br_LT){io.taken := lt}
    is(BrOp.Br_LTU){io.taken := ltu}
    is(BrOp.Br_GE){io.taken := ge}
    is(BrOp.Br_GEU){io.taken := geu}
  }
}

