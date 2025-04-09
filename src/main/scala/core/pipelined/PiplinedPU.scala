package core.pipelined

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import core.Signal._
import core.Colors._

class PassPCBundle extends Bundle {
  val pc = UInt(64.W)
  val pc_4 = UInt(64.W)
}

class PassPCInstBundle extends PassPCBundle {
  val inst = UInt(64.W)
  val notbubble = Bool()
}

class FeadBackPCBundle extends Bundle {
  val pc = UInt(64.W)
  val pc_4 = UInt(64.W)
  val inst = UInt(64.W)
  val taken = Bool()
}

class PCGenModule extends Module{
  val io = IO(new Bundle{
    val out = new PassPCBundle
    val en = Input(Bool())
    val clr = Input(Bool())
  })
  val pcReg = withReset(io.clr){RegInit(0.U(64.W))}
  val pc_4 = Wire(UInt(64.W))
  pc_4 := pcReg + 4.U
  when(io.en){
    pcReg := pc_4
  }
  io.out.pc := pcReg
  io.out.pc_4 := pc_4
  //printf("PC: 0x%x\n", pcReg)
}


class MemWritebackBundle extends Bundle {
  val wb_sel = UInt(2.W)
  val wb_en = Bool()
  val rd_addr = UInt(5.W)
  val pc_4 = UInt(64.W)
  val alu_res = UInt(64.W)
  val mem_res = UInt(64.W)
  val imm_u = UInt(64.W)
  val notbubble = Bool()
}

class ExeMemBundle extends Bundle {
  val wb_sel = UInt(2.W)
  val wb_en = Bool()
  val rd_addr = UInt(5.W)
  val pc_4 = UInt(64.W)
  val alu_res = UInt(64.W)
  //val mem_res = UInt(64.W)
  val imm_u = UInt(64.W)
  val mem_width = UInt(3.W)
  val mem_sig = Bool()
  val mem_we = Bool()
  val rs2_data = UInt(64.W)
  val notbubble = Bool()
}

class PassuInstBundle extends Bundle {
  val src1 = UInt(64.W)
  val src2 = UInt(64.W)
  val alu_op = UInt(4.W)
  val bru_op = UInt(3.W)
  val wb_sel = UInt(2.W)
  val wb_en = Bool()
  val rd_addr = UInt(5.W)
  val pc_4 = UInt(64.W)
  //val alu_res = UInt(64.W)
  //val mem_res = UInt(64.W)
  val imm_u = UInt(64.W)
  val mem_width = UInt(3.W)
  val mem_sig = Bool()
  val mem_we = Bool()
  val rs2_data = UInt(64.W)
  val notbubble = Bool()
}

class FetchDataIO extends Bundle {
  val addr = Output(UInt(64.W))
  val WE = Output(Bool())
  val bfwd = Output(UInt(3.W))
  val sig = Output(Bool())
  val wdata = Output(UInt(64.W))
  val rdata = Input(UInt(64.W))
}

class PUIO extends Bundle{
  val fetchinst = new FetchInstIO
  val fetchdata = new FetchDataIO
}
class PipelineState extends Bundle {
  val Retired = Output(Bool())
  val wbaddr = Output(UInt(5.W))
}

class PUProbe extends Bundle {
    val mcycle = UInt(64.W)
    val IF = new PassPCInstBundle
    val ID = new DecodeModuleProbeIO
    val EXE = new ExeModuleProbeIO
    val MEM = new MemModuleProbeIO
    val WB = new WriteBackModuleProbeIO
    val rf = new RegWatchPort
    val pipelinestate = new PipelineState
}

class PiplinedPU extends Module {
  val io = IO(new PUIO)
  val probe = IO(new PUProbe)
  val mcycle = RegInit(0.U(64.W))
  mcycle := mcycle + 1.U
  //printf("[%d]", mcycle)
  //IF stage
  probe.mcycle := mcycle
  val pcModule = Module(new PCGenModule)
  val ifetchModule = Module(new SimpleFetchWithProbeModule)
  probe.IF <> ifetchModule.probe
  val instQueue = Module(new SingleInstQueue)
  val rf = Module(new PipelinedRegFileWithWatchPort)
  probe.rf <> rf.debug
  val decodeModule = Module(new DecodeModuleWithProbe)
  probe.ID <> decodeModule.probe
  val exeModule = Module(new ExeModuleWithProbe)
  probe.EXE <> exeModule.probe
  val memModule = Module(new MemModuleWithProbe)
  probe.MEM <> memModule.probe
  val wbModule = Module(new WriteBackModuleWithProbe)
  probe.WB <> wbModule.probe
  val decodeexeinitialValue =   (new PassuInstBundle).Lit(
      _.wb_en -> false.B,
      _.wb_sel -> DATA_XXX,
      _.rd_addr -> 0.U,
      _.pc_4 -> 0.U,
      //_.alu_res -> 0.U,
      _.imm_u -> 0.U,
      _.mem_width -> 0.U,
      _.mem_sig -> false.B,
      _.mem_we -> false.B,
      _.rs2_data -> 0.U,
      _.src1 -> 0.U,
      _.src2 -> 0.U,
      _.alu_op -> core.ALU.ALUOp.OP_ADD,
      _.bru_op -> core.BrExe.BrOp.Br_EQ,
      _.notbubble -> false.B,
    )
  val decodeexeRegEN = Wire(Bool())
  val decodeexeRegCLR = Wire(Bool())
  val decodeexeReg = withReset(decodeexeRegCLR){
    RegEnable(decodeModule.io.out,decodeexeinitialValue,decodeexeRegEN)
  }

  val exememReginitialValue =  (new ExeMemBundle).Lit(
      _.wb_en -> false.B,
      _.wb_sel -> DATA_XXX,
      _.rd_addr -> 0.U,
      _.pc_4 -> 0.U,
      _.alu_res -> 0.U,
      _.imm_u -> 0.U,
      _.mem_width -> 0.U,
      _.mem_sig -> false.B,
      _.mem_we -> false.B,
      _.rs2_data -> 0.U,
      _.notbubble -> false.B,
    )
  val exememRegEN = Wire(Bool())
  val exememRegCLR = Wire(Bool())
  val memwbReginitialValue = (new MemWritebackBundle).Lit(
      _.wb_en -> false.B,
      _.wb_sel -> DATA_XXX,
      _.rd_addr -> 0.U,
      _.pc_4 -> 0.U,
      _.alu_res -> 0.U,
      _.mem_res -> 0.U,
      _.imm_u -> 0.U,
      _.notbubble -> false.B,
    )
  val memwbRegEN = Wire(Bool())
  val memwbRegCLR = Wire(Bool())
  //IF
  io.fetchinst <> ifetchModule.io.getInst
  ifetchModule.io.pcIn <> pcModule.io.out
  ifetchModule.io.out <> instQueue.io.in
  //reg
  instQueue.io.out <> decodeModule.io.in
  decodeModule.io.fetchrf <> rf.io.readPort

  //decodeModule.io.out <> decodeexeReg
  
  exeModule.io.in <> decodeexeReg
  //exeModule.io.out <> exememReg
  val exememReg = withReset(exememRegCLR){
    RegEnable(exeModule.io.out,exememReginitialValue,exememRegEN)
  }
  memModule.io.in := exememReg
  memModule.io.getdata <> io.fetchdata
  //memModule.io.out <> memwbReg
  val memwbReg = withReset(memwbRegCLR){
    RegEnable(memModule.io.out,memwbReginitialValue,memwbRegEN)
  }
  wbModule.io.in := memwbReg
  wbModule.io.out <> rf.io.writePort
  /**stall
   * version 1
   * stop instqueue and pc
   * untill data written back
  * */
  val flow_stall = Wire(Bool())
  val rs1_in_pipe = Wire(Bool())
  val rs2_in_pipe = Wire(Bool())

  rs1_in_pipe := (decodeModule.io.fetchrf.src1_addr === exeModule.io.out.rd_addr) && (exeModule.io.in.notbubble) ||
       (decodeModule.io.fetchrf.src1_addr === memModule.io.out.rd_addr) && memModule.io.in.notbubble ||
       (decodeModule.io.fetchrf.src1_addr === wbModule.io.out.rd_addr) && wbModule.io.in.notbubble
  rs2_in_pipe := (decodeModule.io.fetchrf.src2_addr === exeModule.io.out.rd_addr) && exeModule.io.in.notbubble ||
        (decodeModule.io.fetchrf.src2_addr === memModule.io.out.rd_addr) && memModule.io.in.notbubble  ||
        (decodeModule.io.fetchrf.src2_addr === wbModule.io.out.rd_addr) && wbModule.io.in.notbubble
  val (rs1_bypass_able,rs1_from_bypass) = (Wire(Bool()),Wire(UInt(64.W)))
  val (rs2_bypass_able,rs2_from_bypass) = (Wire(Bool()),Wire(UInt(64.W)))
  //just bypass compute-related inst
  when(wbModule.io.out.rd_addr === decodeModule.io.fetchrf.src1_addr && wbModule.io.out.WriteEnable){
    // bypass from wb
    rs1_bypass_able := true.B
    rs1_from_bypass := wbModule.io.out.rd_data
  }.elsewhen(memModule.io.out.rd_addr === decodeModule.io.fetchrf.src1_addr && memModule.io.out.wb_en ){
    //bypass from mem, fetch data will write to regfile
    rs1_bypass_able := memModule.io.in.wb_sel ===  DATA_ALU
    rs1_from_bypass := memModule.io.out.alu_res   
    }.elsewhen(exeModule.io.out.rd_addr === decodeModule.io.fetchrf.src1_addr && exeModule.io.out.wb_en){
    rs1_bypass_able := exeModule.io.out.wb_sel === DATA_ALU
    rs1_from_bypass := exeModule.io.out.alu_res
  }.otherwise{
    rs1_bypass_able := false.B
    rs1_from_bypass := 0.U
  }
  //by pass to rs2
  when(wbModule.io.out.rd_addr === decodeModule.io.fetchrf.src2_addr && wbModule.io.out.WriteEnable){
    // bypass from wb
    rs2_bypass_able := true.B
    rs2_from_bypass := wbModule.io.out.rd_data
  }.elsewhen(memModule.io.out.rd_addr === decodeModule.io.fetchrf.src2_addr && memModule.io.out.wb_en ){
    //bypass from mem, fetch data will write to regfile
    rs2_bypass_able := memModule.io.in.wb_sel ===  DATA_ALU
    rs2_from_bypass := memModule.io.out.alu_res   
    }.elsewhen(exeModule.io.out.rd_addr === decodeModule.io.fetchrf.src2_addr && exeModule.io.out.wb_en){
    rs2_bypass_able := exeModule.io.out.wb_sel === DATA_ALU
    rs2_from_bypass := exeModule.io.out.alu_res
  }.otherwise{
    rs2_bypass_able := false.B
    rs2_from_bypass := 0.U
  }

  decodeModule.io.bypass.rs1_bypass_able := rs1_bypass_able
  decodeModule.io.bypass.rs1_from_bypass := rs1_from_bypass
  decodeModule.io.bypass.rs2_bypass_able := rs2_bypass_able
  decodeModule.io.bypass.rs2_from_bypass := rs2_from_bypass
   

  flow_stall := (rs1_in_pipe && decodeModule.io.fetchrf.src1_addr.orR) && (!rs1_bypass_able)  || (rs2_in_pipe && decodeModule.io.fetchrf.src2_addr.orR) && (!rs2_bypass_able)
  when(flow_stall){
    //stop pcModule and instQueue
    pcModule.io.en := false.B
    pcModule.io.clr := false.B
    instQueue.io.clr := false.B
    instQueue.io.en := false.B
    
    decodeexeRegCLR := true.B
    decodeexeRegEN := false.B
    
    exememRegCLR := (false).B
    exememRegEN := (true).B
    memwbRegCLR := false.B
    memwbRegEN := true.B
  }.otherwise{
    pcModule.io.en := true.B
    pcModule.io.clr := false.B
    instQueue.io.clr := false.B
    instQueue.io.en := true.B
    decodeexeRegCLR := false.B
    decodeexeRegEN := true.B
    exememRegCLR := false.B
    exememRegEN := true.B
    memwbRegEN := true.B
    memwbRegCLR := false.B
  }
  //whether writeback finished
  val wbdone = RegInit(false.B)
  wbdone := wbModule.io.out.WriteEnable && memwbReg.notbubble
  probe.pipelinestate.Retired := wbdone  
  val wbaddrReg = RegInit(0.U(5.W))
  when(wbModule.io.out.WriteEnable && memwbReg.notbubble) {
    wbaddrReg := wbModule.io.out.rd_addr
  }
  probe.pipelinestate.wbaddr := wbaddrReg
}
