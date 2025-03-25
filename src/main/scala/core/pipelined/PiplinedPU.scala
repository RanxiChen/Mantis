package core.pipelined

import chisel3._
import chisel3.util._

class PassPCIO extends Bundle {
  val pc = UInt(64.W)
}

class PassPCInstIO extends Bundle {
  val pc = UInt(64.W)
  val inst = UInt(64.W)
}

class PCGenModule extends Module{
  val io = new PassPCIO
  val pcReg = RegInit(0.U(64.W))
  pcReg := pcReg + 4.U
  printf("PC: 0x%x\n", pcReg)
  io.pc := pcReg
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
  io.fetchinst <> ifetchModule.io.getInst
  ifetchModule.io.pcIn <> pcModule.io
  ifetchModule.io.out <> instQueue.io.in
}