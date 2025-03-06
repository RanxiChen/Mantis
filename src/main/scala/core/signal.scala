package core

object Signal {
  val IMM_I = 0.U(3.W)
  val IMM_S = 1.U(3.W)
  val IMM_B = 2.U(3.W)
  val IMM_U = 3.U(3.W)
  val IMM_J = 4.U(3.W)

  // PC_sel
  val PC_4 = 0.U(2.W)
  val PC_Imm = 1.U(2.W)
  val PC_jalr = 2.U(2.W)
  val PC_bru = 3.U(2.W)

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
  val W = true.B
  val R = false.B
  val X = false.B

  //Regfile Write
  val W = true.B
  val R = false.B
  val X = false.B

  //Reg data source
  val ALU = 0.U(2.W)
  val RS2 = 1.U(2.W)
  val MEM = 2.U(2.W)
  val PC4 = 3.U(2.W)
  val XXX = 0.U(2.W)

}
