package core
import chisel3._

object Signal {
  val IMM_I = 0.U(3.W)
  val IMM_S = 1.U(3.W)
  val IMM_B = 2.U(3.W)
  val IMM_U = 3.U(3.W)
  val IMM_J = 4.U(3.W)

  // PC_sel
  val PC_4 = 0.U(2.W)
  val PC_IMM = 1.U(2.W)
  val PC_JRL = 2.U(2.W)
  val PC_BRU = 3.U(2.W)

  //ALU_src1
  val A_RS1 = 0.U(1.W)
  val A_XXX = 0.U(1.W)
  val A_PC = 1.U(1.W)

  //ALU_src2
  val B_XXX = 0.U(2.W)
  val B_RS2 = 1.U(2.W)
  val B_IMM = 2.U(2.W)
  val B_PC  = 3.U(2.W)

  //Mem width
  val X = "b000".U(3.W)
  val B = "b000".U(3.W)
  val H = "b001".U(3.W)
  val W = "b010".U(3.W)
  val D = "b100".U(3.W)

  //Mem sig
  val U = false.B
  val S = true.B

  //Mem WE
  val Mem_W = true.B
  val Mem_R = false.B
  val Mem_X = false.B

  //Regfile Write
  val Reg_W = true.B
  val Reg_R = false.B
  val Reg_X = false.B

  //WB source
  val DATA_ALU = 0.U(2.W)
  val DATA_IMM = 1.U(2.W)
  val DATA_MEM = 2.U(2.W)
  val DATA_PC4 = 3.U(2.W)
  val DATA_XXX = 0.U(2.W)

}
