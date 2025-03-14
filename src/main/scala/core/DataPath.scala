package core

import chisel3._
import chisel3.util._


class DataPathIO extends Bundle {
  val ctrl = Flipped(new CtrlIO)
  val fetch = Flipped(new core.Mem.IFMemIO)
  val memory = Flipped(new core.Mem.MemIO)
}

class DataPathProbe extends Bundle {
  val rf = new core.RFProbe
  val alu_src1 = Output(UInt(64.W))
  val alu_src2 = Output(UInt(64.W))
  val alu_res = Output(UInt(64.W))
  val pc = Output(UInt(64.W))
  val inst = Output(UInt(32.W))
  val imm = Output(UInt(64.W))
  val wb_data = Output(UInt(64.W))
}


class DataPath(debug:Boolean=false,rfinit:String = "conf/reg.ini") extends Module {
  val io = IO(new Bundle{
    val dpIO = new DataPathIO
    val probe = if(debug) Some(new DataPathProbe) else None
  })
  val rf = Module(new core.RegFileModule(debug,rfinit))
  val alu = Module(new core.ALU.ALU)

  if(debug) {io.probe.get.rf <> rf.io.probe.get}
  
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
      (io.dpIO.ctrl.pc_sel === Signal.PC_4  ) -> pc_4,
      (io.dpIO.ctrl.pc_sel === Signal.PC_IMM) -> pc_imm,
      (io.dpIO.ctrl.pc_sel === Signal.PC_JLR) -> pc_jlr,
      (io.dpIO.ctrl.pc_sel === Signal.PC_BRU) -> pc_bru
    )
  )
  if(debug){io.probe.get.pc := pcReg}

  pcReg := pc_NextWire
  printf("PC = 0x%x\n",pcReg)

  //IF
  val inst = Wire(UInt(64.W))
  inst := io.dpIO.fetch.inst
  io.dpIO.fetch.addr := pcReg

  io.dpIO.ctrl.inst := inst
  printf("[IF] inst = %x\n",inst)

  if(debug){io.probe.get.inst := inst}


  //decode inst
  val inst_rs1 = Wire(UInt(5.W))
  val inst_rs2 = Wire(UInt(5.W))
  val inst_rd = Wire(UInt(5.W))

  inst_rs1 := inst(19,15)
  inst_rs2 := inst(24,20)
  inst_rd := inst(11,7)

  //RegRead, prepare for alu
  

  rf.io.rf.rs1_addr := inst_rs1
  rf.io.rf.rs2_addr := inst_rs2

  val immgen = Module(new ImmGen_RV64I)
  immgen.io.inst := inst

  val immValue = Wire(UInt(64.W))
  immValue := immgen.io.imm
  immgen.io.sel := io.dpIO.ctrl.imm_sel
  printf("[EXE] immValue = %x\n",immValue)
  printf("[INFO] imm_sel = %x\n",io.dpIO.ctrl.imm_sel)

  if(debug){io.probe.get.imm := immValue}

  //Exe
  val alu_src1_data = Wire(UInt(64.W))
  val alu_src2_data = Wire(UInt(64.W))

  alu_src1_data := MuxCase(0.U,
    IndexedSeq(
      (io.dpIO.ctrl.alu_src1 === Signal.A_RS1) -> rf.io.rf.rs1_data,
      (io.dpIO.ctrl.alu_src1 === Signal.A_PC ) -> pcReg,
      (io.dpIO.ctrl.alu_src1 === Signal.A_XXX ) -> 0.U
    )
  )
  printf("[EXE] alu_src1_data = %x\n",alu_src1_data)

  alu_src2_data := MuxCase(0.U,
    IndexedSeq(
      (io.dpIO.ctrl.alu_src2 === Signal.B_RS2) -> rf.io.rf.rs2_data,
      (io.dpIO.ctrl.alu_src2 === Signal.B_IMM) -> immValue,
      (io.dpIO.ctrl.alu_src2 === Signal.B_PC ) -> pcReg,
      (io.dpIO.ctrl.alu_src2 === Signal.B_XXX ) -> 0.U
    )
  )
  printf("[EXE] alu_src2_data = %x\n",alu_src2_data)

  alu.io.src1 := alu_src1_data
  alu.io.src2 := alu_src2_data
  alu.io.op := io.dpIO.ctrl.alu_op

  if(debug){
    io.probe.get.alu_src1 := alu_src1_data
    io.probe.get.alu_src2 := alu_src2_data
    io.probe.get.alu_res := alu.io.out
  }

  //BrU
  val bru = Module(new BrExe.BrU)
  bru.io.rs1 := rf.io.rf.rs1_data
  bru.io.rs2 := rf.io.rf.rs2_data
  bru.io.BrOp := io.dpIO.ctrl.bru_op

  val alu_res = Wire(UInt(64.W))
  alu_res := alu.io.out
  printf("[EXE] alu_res = %x\n",alu_res)

  pc_imm := alu_res
  pc_jlr := alu_res & ( Fill(63,true.B) ## false.B )
  pc_bru := Mux(bru.io.taken, alu_res, pc_4)

  //Mem

  io.dpIO.memory.addr := alu_res
  io.dpIO.memory.bfwd := io.dpIO.ctrl.mem_width
  io.dpIO.memory.sig := io.dpIO.ctrl.mem_sig
  io.dpIO.memory.wdata := rf.io.rf.rs2_data
  io.dpIO.memory.WE := io.dpIO.ctrl.mem_we
  if(debug){
    printf("[MEM] addr = %x\n",io.dpIO.memory.addr)
    printf("[MEM] wdata = %x\n",io.dpIO.memory.wdata)
    printf("[MEM] WE =  %x | sig = %x | bfwd = %x\n",io.dpIO.memory.WE,io.dpIO.memory.sig,io.dpIO.memory.bfwd)
  }

  //WriteBack
  val wb_data = Wire(UInt(64.W))
  wb_data := MuxCase(0.U,
    IndexedSeq(
      (io.dpIO.ctrl.wb_sel === Signal.DATA_ALU ) -> alu_res,
      (io.dpIO.ctrl.wb_sel === Signal.DATA_MEM ) -> io.dpIO.memory.rdata,
      (io.dpIO.ctrl.wb_sel === Signal.DATA_IMM ) -> immValue,
      (io.dpIO.ctrl.wb_sel === Signal.DATA_PC4 ) -> pc_4,
      (io.dpIO.ctrl.wb_sel === Signal.DATA_RS2 ) -> rf.io.rf.rs2_data,
      (io.dpIO.ctrl.wb_sel === Signal.DATA_XXX ) -> 0.U
    )
  )
  if(debug){io.probe.get.wb_data := wb_data}
  rf.io.rf.rd_addr := inst_rd
  rf.io.rf.rd_data := wb_data
  rf.io.rf.W_enable := io.dpIO.ctrl.wb_en
  
  if(debug){
    printf("[WB] Write %d to x%x, with en:%x\n",wb_data,rf.io.rf.rd_addr,io.dpIO.ctrl.wb_en)
  }

}
