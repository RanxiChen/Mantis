package core.pipelined

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._

class PassPCBundle extends Bundle {
  val pc = UInt(64.W)
  val pc_4 = UInt(64.W)
}

class PassPCInstBundle extends Bundle {
  val pc = UInt(64.W)
  val pc_4 = UInt(64.W)
  val inst = UInt(64.W)
}

class PCGenModule extends Module{
  val io = IO(new PassPCBundle)
  val pcReg = RegInit(0.U(64.W))
  val pc_4 = Wire(UInt(64.W))
  pc_4 := pcReg + 4.U
  pcReg := pc_4
  io.pc := pcReg
  io.pc_4 := pc_4
  printf("PC: 0x%x\n", pcReg)
}


class PUIO extends Bundle{
  val fetchinst = new FetchInstIO
}

class PiplinedPU extends Module{
  val io = IO(new PUIO)
  //IF stage
  val pcModule = Module(new PCGenModule)
  val ifetchModule = Module(new SimpleFetchModule)
  val instQueue = Module(new SingleInstQueue)
  val rf = Module(new PipelinedRegFileImpl)
  io.fetchinst <> ifetchModule.io.getInst
  ifetchModule.io.pcIn <> pcModule.io
  ifetchModule.io.out <> instQueue.io.in
  //decode stage
  val decodememReg = RegInit(
    (new PassuInstBundle).Lit(
      _.src1 -> 0.U,
      _.src2 -> 0.U,
      _.alu_op -> core.ALU.ALUOp.OP_ADD,
      _.bru_op -> core.BrExe.BrOp.Br_EQ,
      _.mem_width -> 0.U,
      _.mem_sig -> false.B,
      _.mem_we -> false.B,
      _.wb_sel -> 0.U,
      _.wb_en -> false.B,
      _.pc_sel -> 0.U,
      _.rs2_data -> 0.U,
      _.rd_addr -> 0.U,
      _.pc_4 -> 0.U      
    )
  )
  val decodeModule = Module(new DecodeModule)
  decodeModule.io.in <> instQueue.io.out
  decodeModule.io.out <> decodememReg
  decodeModule.io.fetchrf <> rf.io.readPort
}