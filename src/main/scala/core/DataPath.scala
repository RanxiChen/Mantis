package core

import chisel3._
import chisel3.util._

class DataPathIO extends Bundle {
  val ctrl = Flipped(new CtrlIO)
  val fetch = Flipped(new core.Mem.IFMemIO)
  val memory = Flipped(new core.Mem.MemIO)
}

class DataPath_single extends Module {
  val io = IO(new DataPathIO)
  val rf = Module(new core.RegFile.RegFile(true))
  val alu = Module(new core.ALU.ALU)
  
  //PC
  val pcReg = RegInit(0.U(64.W))

  val pc_4 = Wire(UInt(64.W))
  pc_4 := pcReg + 4.U

  val pc_imm = Wire(UInt(64.W))
  val pc_jrl = Wire(UInt(64.W))
  val pc_bru = Wire(UInt(64.W))

  pcReg := MuxCase(pc_4,
    IndexedSeq(
      (io.ctrl.pc_sel === Signal.PC_4  ) -> pc_4,
      (io.ctrl.pc_sel === Signal.PC_IMM) -> pc_imm,
      (io.ctrl.pc_sel === Signal.PC_JRL) -> pc_jrl,
      (io.ctrl.pc_sel === Signal.PC_BRU) -> pc_bru
    )
  )

  //IF
  val inst = Wire(UInt(64.W))
  inst := io.fetch.inst
  io.fetch.addr := pcReg

  io.ctrl.inst := inst

  //decode inst
  val inst_rs1 = Wire(UInt(5.W))
  val inst_rs2 = Wire(UInt(5.W))
  val inst_rd = Wire(UInt(5.W))

  inst_rs1 := inst(19,15)
  inst_rs2 := inst(24,20)
  inst_rd := inst(11,7)

  //RegRead, prepare for alu
  

  rf.io.rs1_addr := inst_rs1
  rf.io.rs2_addr := inst_rs2

  val immgen = Module(new ImmGen_RV64I)
  immgen.io.inst := inst

  val immValue = Wire(UInt(64.W))
  immValue := immgen.io.imm
  immgen.io.sel := io.ctrl.imm_sel

  //Exe
  val alu_src1_data = Wire(UInt(64.W))
  val alu_src2_data = Wire(UInt(64.W))

  alu_src1_data := MuxCase(0.U,
    IndexedSeq(
      (io.ctrl.alu_src1 === Signal.A_RS1) -> rf.io.rs1_data,
      (io.ctrl.alu_src1 === Signal.A_PC ) -> pcReg,
      (io.ctrl.alu_src1 === Signal.A_XXX ) -> 0.U
    )
  )

  alu_src2_data := MuxCase(0.U,
    IndexedSeq(
      (io.ctrl.alu_src2 === Signal.B_RS2) -> rf.io.rs2_data,
      (io.ctrl.alu_src2 === Signal.B_IMM) -> immValue,
      (io.ctrl.alu_src2 === Signal.B_PC ) -> pcReg,
      (io.ctrl.alu_src2 === Signal.B_XXX ) -> 0.U
    )
  )

  alu.io.src1 := alu_src1_data
  alu.io.src2 := alu_src2_data
  alu.io.op := io.ctrl.alu_op

  //BrU
  val bru = Module(new BrExe.BrU)
  bru.io.rs1 := rf.io.rs1_data
  bru.io.rs2 := rf.io.rs2_data
  bru.io.BrOp := io.ctrl.bru_op

  val alu_res = Wire(UInt(64.W))
  alu_res := alu.io.out

  pc_imm := alu_res
  pc_jrl := alu_res & ( Fill(63,true.B) ## false.B )
  pc_bru := Mux(bru.io.taken, alu_res, pc_4)

  //Mem

  io.memory.addr := alu_res
  io.memory.bfwd := io.ctrl.MemWidth
  io.memory.sig := io.ctrl.MemSig
  io.memory.wdata := rf.io.rs2_data
  io.memory.WE := io.ctrl.MemWE

  //WriteBack
  val wb_data = Wire(UInt(64.W))
  wb_data := MuxCase(0.U,
    IndexedSeq(
      (io.ctrl.RfSel === Signal.DATA_ALU ) -> alu_res,
      (io.ctrl.RfSel === Signal.DATA_MEM ) -> io.memory.rdata,
      (io.ctrl.RfSel === Signal.DATA_IMM ) -> immValue,
      (io.ctrl.RfSel === Signal.DATA_PC4 ) -> pc_4,
      (io.ctrl.RfSel === Signal.DATA_XXX ) -> 0.U
    )
  )
  rf.io.rd_addr := inst_rd
  rf.io.rd_data := wb_data
  rf.io.W_enable := io.ctrl.RegWE
  

}
