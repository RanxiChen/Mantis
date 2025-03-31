package core.pipelined

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import core.Signal._

class PassPCBundle extends Bundle {
  val pc = UInt(64.W)
  val pc_4 = UInt(64.W)
}

class PassPCInstBundle extends PassPCBundle {
  val inst = UInt(64.W)
}

class FeadBackPCBundle extends Bundle {
  val pc = UInt(64.W)
  val pc_4 = UInt(64.W)
  val inst = UInt(64.W)
  val taken = Bool()
}

class PCGenModule extends Module{
  val io = IO(new PassPCBundle)
  val pcReg = RegInit(0.U(64.W))
  val pc_4 = Wire(UInt(64.W))
  pc_4 := pcReg + 4.U
  pcReg := pc_4
  io.pc := pcReg
  io.pc_4 := pc_4
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

trait PUProbe {
  val probe = IO(new Bundle{
    val mcycle = UInt(64.W)
    val IF = new PassPCInstBundle
    val ID = new PassuInstBundle
    val EXE = new ExeMemBundle
    val MEM = new MemWritebackBundle
    val WB = new MemWritebackBundle
  })
}

class PiplinedPU(debug:Boolean=false) extends Module with PUProbe {
  val io = IO(new PUIO)
  val mcycle = RegInit(0.U(64.W))
  mcycle := mcycle + 1.U
  //printf("[%d]", mcycle)
  //IF stage
  probe.mcycle := mcycle
  val pcModule = Module(new PCGenModule)
  val ifetchModule = FetchModule(debug)
  val instQueue = Module(new SingleInstQueue)
  val rf = Module(new PipelinedRegFileImpl)
  val decodeModule = Module(new DecodeModule)
  val exeModule = ExeModule(debug)
  val memModule = MemModule(debug)
  val wbModule = WriteBackModule(debug)
  val decodeexeReg = RegInit(
    (new PassuInstBundle).Lit(
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
      _.bru_op -> core.BrExe.BrOp.Br_EQ
    )
  )
  val exememReg = RegInit(
    (new ExeMemBundle).Lit(
      _.wb_en -> false.B,
      _.wb_sel -> DATA_XXX,
      _.rd_addr -> 0.U,
      _.pc_4 -> 0.U,
      _.alu_res -> 0.U,
      _.imm_u -> 0.U,
      _.mem_width -> 0.U,
      _.mem_sig -> false.B,
      _.mem_we -> false.B,
      _.rs2_data -> 0.U
    )
  )
  val memwbReg = RegInit(
    (new MemWritebackBundle).Lit(
      _.wb_en -> false.B,
      _.wb_sel -> DATA_XXX,
      _.rd_addr -> 0.U,
      _.pc_4 -> 0.U,
      _.alu_res -> 0.U,
      _.mem_res -> 0.U,
      _.imm_u -> 0.U
    )
  )
  io.fetchinst <> ifetchModule.io.getInst
  ifetchModule.io.pcIn <> pcModule.io
  ifetchModule.io.out <> instQueue.io.in
  instQueue.io.out <> decodeModule.io.in
  decodeModule.io.fetchrf <> rf.io.readPort
  decodeModule.io.out <> decodeexeReg
  exeModule.io.in <> decodeexeReg
  exeModule.io.out <> exememReg
  memModule.io.in <> exememReg
  memModule.io.getdata <> io.fetchdata
  memModule.io.out <> memwbReg
  wbModule.io.in <> memwbReg
  wbModule.io.out <> rf.io.writePort
  
}