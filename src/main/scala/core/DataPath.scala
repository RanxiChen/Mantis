package core

import chisel3._
import chisel3.util._

class RFProbeIO extends Bundle {
  val content = Output(Vec(32,UInt(64.W)))
}
class DataPathIO(debug:Boolean=false) extends Bundle {
  val ctrl = Flipped(new CtrlIO)
  val fetch = Flipped(new core.Mem.IFMemIO)
  val memory = Flipped(new core.Mem.MemIO)
  val rfprobe = if(debug)Some(new RFProbeIO) else None
}


class DataPath_single(debug:Boolean=false) extends Module {
  val io = IO(new DataPathIO(debug))
  val rf = Module(new core.RegFile.RegFile(debug))
  val alu = Module(new core.ALU.ALU)
  if(debug){
    io.rfprobe.get.content <> rf.io.content.get
  }
  
  //PC
  val pcReg = RegInit(0.U(64.W))

  val pc_4 = Wire(UInt(64.W))
  pc_4 := pcReg + 4.U

  val pc_imm = Wire(UInt(64.W))
  val pc_jlr = Wire(UInt(64.W))
  val pc_bru = Wire(UInt(64.W))

  val pc_NextWire = Wire(UInt(64.W))
  pc_NextWire := MuxCase(pc_4,
    IndexedSeq(
      (io.ctrl.pc_sel === Signal.PC_4  ) -> pc_4,
      (io.ctrl.pc_sel === Signal.PC_IMM) -> pc_imm,
      (io.ctrl.pc_sel === Signal.PC_JLR) -> pc_jlr,
      (io.ctrl.pc_sel === Signal.PC_BRU) -> pc_bru
    )
  )

  pcReg := pc_NextWire
  printf("PC = 0x%x\n",pcReg)
  printf("[INFO] pc_next = %x\n",pc_NextWire)

  //IF
  val inst = Wire(UInt(64.W))
  inst := io.fetch.inst
  io.fetch.addr := pcReg

  io.ctrl.inst := inst
  printf("[IF] inst = %x\n",inst)


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
  printf("[EXE] immValue = %x\n",immValue)
  printf("[INFO] imm_sel = %x\n",io.ctrl.imm_sel)

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
  printf("[EXE] alu_src1_data = %x\n",alu_src1_data)

  alu_src2_data := MuxCase(0.U,
    IndexedSeq(
      (io.ctrl.alu_src2 === Signal.B_RS2) -> rf.io.rs2_data,
      (io.ctrl.alu_src2 === Signal.B_IMM) -> immValue,
      (io.ctrl.alu_src2 === Signal.B_PC ) -> pcReg,
      (io.ctrl.alu_src2 === Signal.B_XXX ) -> 0.U
    )
  )
  printf("[EXE] alu_src2_data = %x\n",alu_src2_data)

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
  printf("[EXE] alu_res = %x\n",alu_res)

  pc_imm := alu_res
  pc_jlr := alu_res & ( Fill(63,true.B) ## false.B )
  pc_bru := Mux(bru.io.taken, alu_res, pc_4)

  //Mem

  io.memory.addr := alu_res
  io.memory.bfwd := io.ctrl.mem_width
  io.memory.sig := io.ctrl.mem_sig
  io.memory.wdata := rf.io.rs2_data
  io.memory.WE := io.ctrl.mem_we
  if(debug){
    printf("[MEM] addr = %x\n",io.memory.addr)
    printf("[MEM] wdata = %x\n",io.memory.wdata)
    printf("[MEM] WE =  %x | sig = %x | bfwd = %x\n",io.memory.WE,io.memory.sig,io.memory.bfwd)
  }

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
  
  if(debug){
    printf("[WB] Write %d to x%x, with en:%x\n",wb_data,rf.io.rd_addr,io.ctrl.wb_en)
  }

}
