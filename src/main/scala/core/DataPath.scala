package core

import chisel3._
import chisel3.util._

class DataPathIO(debug:Boolean=false) extends Bundle {
  val ctrl = Flipped(new CtrlIO)
  val fetch = Flipped(new core.Mem.IFMemIO)
  val memory = Flipped(new core.Mem.MemIO)
  val rfporbe = if(debug)Some(new RFProbeIO) else None
}

class RFProbeIO extends Bundle{
  val addr = Input(UInt(5.W))
  val data = Output(UInt(64.W))
}
class DataPath_single(debug:Boolean=false) extends Module {
  val io = IO(new DataPathIO)
  val rf = Module(new core.RegFile.RegFile(debug))
  val alu = Module(new core.ALU.ALU)
  if(debug){
    (io.rfporbe.get).data := (rf.io.content.get)(io.rfporbe.get.addr)
  }
  
  //PC
  val pcReg = RegInit(0.U(64.W))

  val pc_4 = Wire(UInt(64.W))
  pc_4 := pcReg + 4.U

  val pc_imm = Wire(UInt(64.W))
  val pc_jlr = Wire(UInt(64.W))
  val pc_bru = Wire(UInt(64.W))

  pcReg := MuxCase(pc_4,
    IndexedSeq(
      (io.ctrl.pc_sel === Signal.PC_4  ) -> pc_4,
      (io.ctrl.pc_sel === Signal.PC_IMM) -> pc_imm,
      (io.ctrl.pc_sel === Signal.PC_JLR) -> pc_jlr,
      (io.ctrl.pc_sel === Signal.PC_BRU) -> pc_bru
    )
  )
  println(f"PC = ${pcReg.litValue}%016x")

  //IF
  val inst = Wire(UInt(64.W))
  inst := io.fetch.inst
  io.fetch.addr := pcReg

  io.ctrl.inst := inst
  println(f"[IF] inst = ${inst.litValue}%08x")

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
  println(f"[EXE] alu_src1_data = ${alu_src1_data.litValue}%016x")

  alu_src2_data := MuxCase(0.U,
    IndexedSeq(
      (io.ctrl.alu_src2 === Signal.B_RS2) -> rf.io.rs2_data,
      (io.ctrl.alu_src2 === Signal.B_IMM) -> immValue,
      (io.ctrl.alu_src2 === Signal.B_PC ) -> pcReg,
      (io.ctrl.alu_src2 === Signal.B_XXX ) -> 0.U
    )
  )
  println(f"[EXE] alu_src2_data = ${alu_src2_data.litValue}%016x")

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
  println(f"[EXE] alu_res = ${alu_res.litValue}%016x")

  pc_imm := alu_res
  pc_jlr := alu_res & ( Fill(63,true.B) ## false.B )
  pc_bru := Mux(bru.io.taken, alu_res, pc_4)

  //Mem

  io.memory.addr := alu_res
  io.memory.bfwd := io.ctrl.mem_width
  io.memory.sig := io.ctrl.mem_sig
  io.memory.wdata := rf.io.rs2_data
  io.memory.WE := io.ctrl.mem_we
  println(f"[MEM] addr = ${io.memory.addr.litValue}%016x")
  println(f"[MEM] wdata = ${io.memory.wdata.litValue}%016x")
  println(f"[MEM] WE = ${io.memory.WE.litValue}")
  println(f"[MEM] sig = ${io.memory.sig.litValue}")
  println(f"[MEM] bfwd = ${io.memory.bfwd.litValue}")

  //WriteBack
  val wb_data = Wire(UInt(64.W))
  wb_data := MuxCase(0.U,
    IndexedSeq(
      (io.ctrl.wb_sel === Signal.DATA_ALU ) -> alu_res,
      (io.ctrl.wb_sel === Signal.DATA_MEM ) -> io.memory.rdata,
      (io.ctrl.wb_sel === Signal.DATA_IMM ) -> immValue,
      (io.ctrl.wb_sel === Signal.DATA_PC4 ) -> pc_4,
      (io.ctrl.wb_sel === Signal.DATA_RS2 ) -> rf.io.rs2_data,
      (io.ctrl.wb_sel === Signal.DATA_XXX ) -> 0.U
    )
  )
  rf.io.rd_addr := inst_rd
  rf.io.rd_data := wb_data
  rf.io.W_enable := io.ctrl.wb_en
  println(f"[WB] rd_addr = ${rf.io.rd_addr.litValue}%016x")
  println(f"[WB] wb_data = ${wb_data.litValue}%016x")
  println(f"[WB] W_enable = ${io.ctrl.wb_en.litValue}")

}
