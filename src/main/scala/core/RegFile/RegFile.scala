package core.RegFile

import chisel3._

/**Firstly, used for multi-cycles, but not pipeline
 * In every period, just read(1+) or write
 * In case, reading more than need do not influent 
 * the core, so use 2 read port, 1 write port
 */

class RegFileIO(val debug:Boolean = false) extends Bundle {
  val rd_addr = Input(UInt(5.W))
  val rs1_addr = Input(UInt(5.W))
  val rs2_addr = Input(UInt(5.W))
  val W_enable = Input(Bool()) //Write when true
  val rd_data = Output(UInt(64.W))
  val rs1_data = Output(UInt(64.W))
  val rs2_data = Output(UInt(64.W))
  val content = if(debug) Some(Output(Vec(32,UInt(64.W)))) else None
}

class RegFile(val debug:Boolean = false) extends Module {
  val io = IO(new RegFileIO(debug))
  val regfile = Vec(32, RegInit(0.U(64.W)))
  //Read, not need other process
  io.rs1_data := regfile(io.rs1_addr)
  io.rs2_data := regfile(io.rs2_addr)
  
  regfile(0) := 0.U //hard connect to zero
  when(io.W_enable && io.rd_addr =/= 0.U) {
    regfile(io.rd_addr) := io.rd_data
  }
  if(debug){
    io.content.get := regfile
  }
}


