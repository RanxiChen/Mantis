package core.pipelined

import chisel3._
import chisel3.util._

class RegFileReadPort extends Bundle {
  val src1_addr = Input(UInt(5.W))
  val src2_addr = Input(UInt(5.W))
  val src1_data = Output(UInt(64.W))
  val src2_data = Output(UInt(64.W))
}

class RegFileWritePort extends Bundle {
  val rd_addr = Input(UInt(5.W))
  val rd_data = Input(UInt(64.W))
  val WriteEnable = Input(Bool())
}

class RegAccPort extends Bundle{
  val regacc_vld = Input(Bool())
  val regacc_idx = Input(UInt(5.W))
  val regacc_write = Input(Bool())
  val regacc_data = Input(UInt(64.W))
  val regacc_resp_vld = Output(Bool())
  val regacc_resp_data = Output(UInt(64.W))
}
class RegWatchPort extends Bundle {
   val addr = Input(UInt(5.W))
   val data = Output(UInt(64.W))
 }

class PipelinedRegFileImpl extends Module {
  val io = IO(new Bundle{
    val readPort = new RegFileReadPort
    val writePort = new RegFileWritePort
  })
  val rf = RegInit(VecInit(Seq.fill(32)(0.U(64.W)))
  )
  when(io.writePort.WriteEnable && io.writePort.rd_addr =/= 0.U){
    rf(io.writePort.rd_addr) := io.writePort.rd_data
  }
  /*
  io.readPort.src1_data := rf(io.readPort.src1_addr)
  io.readPort.src2_data := rf(io.readPort.src2_addr)
  */
  io.readPort.src1_data := Mux(
    io.readPort.src1_addr === 0.U,
    0.U,
    Mux(io.readPort.src1_addr === io.writePort.rd_addr && io.writePort.WriteEnable, io.writePort.rd_data, rf(io.readPort.src1_addr))
  )
  io.readPort.src2_data := Mux(
    io.readPort.src2_addr === 0.U,
    0.U,
    Mux(io.readPort.src2_addr === io.writePort.rd_addr && io.writePort.WriteEnable, io.writePort.rd_data, rf(io.readPort.src2_addr))
  )
}

class PipelinedRegFileWithWatchPort extends PipelinedRegFileImpl{
  val debug = IO(new RegWatchPort)
  debug.data := rf(debug.addr)
  val wdone = RegInit(false.B)
  wdone := io.writePort.WriteEnable
}
