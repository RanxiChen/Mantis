package core

import chisel3._
import scala.collection.mutable.ArrayBuffer

class RFIO extends Bundle {
  val rd_addr = Input(UInt(5.W))
  val rs1_addr = Input(UInt(5.W))
  val rs2_addr = Input(UInt(5.W))
  val W_enable = Input(Bool()) //Write when true
  val rd_data = Input(UInt(64.W))
  val rs1_data = Output(UInt(64.W))
  val rs2_data = Output(UInt(64.W))
}

class RFProbe extends Bundle {
  val addr = Input(UInt(5.W))
  val data = Output(UInt(64.W))
}

class RegFileModule(val debug:Boolean = false, val initFile:String = "conf/reg.ini") extends Module {
  val io = IO(new Bundle{
    val rf = new RFIO
    val probe = if(debug) Some(new RFProbe) else None
  })
  val initSeq = ArrayBuffer.fill(32)(0.U(64.W))
  if(initFile != ""){
    val initPairs = os.read.lines(os.pwd/os.RelPath(initFile)).map(_.split("=")).map(item => (item(0),BigInt(item(1).drop(2),16)))
    initPairs.foreach(pair => initSeq(tool.RegAlias.rfalias(pair._1) ) = pair._2.U(64.W))
  }

  val regfile = RegInit(VecInit(initSeq.toSeq))
  io.rf.rs1_data := regfile(io.rf.rs1_addr)
  io.rf.rs2_data := regfile(io.rf.rs2_addr)

  regfile(0) := 0.U //hard connect to zero
  when(io.rf.W_enable && io.rf.rd_addr =/= 0.U) {
    regfile(io.rf.rd_addr) := io.rf.rd_data
  }

  if(debug){
    io.probe.get.data := regfile(io.probe.get.addr)
  }

}


