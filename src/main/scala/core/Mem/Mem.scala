package core.Mem

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline


class IFMemIO extends Bundle{
  val inst = Output(UInt(64.W))
  val addr = Input(UInt(64.W))
}

class MemIO extends Bundle {
  val WE = Input(Bool())
  val addr = Input(UInt(64.W))
  val bfwd = Input(UInt(3.W))
  val sig = Input(Bool()) // 0 for u; 1 for s
  val wdata = Input(UInt(64.W))
  val rdata = Output(UInt(64.W))
}

class MemProbeIO extends Bundle {
  val addr = Input(UInt(64.W))
  val data = Output(UInt(8.W))
}

class MainMem(nkib:Int)(HexPath:String = "misc/Mem/rom.hex",HexMapFile:String = "")(debug:Boolean=false) extends Module{
  val io = IO(new Bundle {
    val IFPort = new IFMemIO
    val MemPort = new MemIO
    val ProbePort = if(debug) Some(new MemProbeIO) else None
  })
  //val content = RegInit(VecInit(Seq.fill(nkib*1024)(0.U(8.W)))) // byte-addressed 
  // TODO: how about byte-address, but orgnized by word-address

  val raw_hex_path = os.pwd / os.RelPath(HexPath)
  //println(s"rom path is :${raw_hex_path}")
  val raw_hex_Seq:Seq[BigInt] = os.read.lines(raw_hex_path).map(_.grouped(2).toList.reverse).flatten.map(BigInt(_,16)&0xff)
  //println(s"rom size is :${raw_hex_Seq.length} bytes")
  println("main_memory size is :"+nkib*1024+" bytes")
  var raw_mem_Seq :Seq[BigInt] = raw_hex_Seq ++ Seq.fill(nkib*1024-raw_hex_Seq.length)(0)
  if(HexMapFile != ""){
    val hexMap = tool.MemHex.hexmapfromfile(HexMapFile)
    for((k,v) <- hexMap){
      raw_mem_Seq = raw_mem_Seq.updated(k,BigInt(v))
    }
  }

  //val content = RegInit(VecInit(raw_hex_Seq.map(_.U(8.W)) ++ Seq.fill(nkib*1024-raw_hex_Seq.length)(0.U(8.W)) ) )
  val content = RegInit(VecInit(raw_mem_Seq.map(_.U(8.W))) )
  val realWidth = math.ceil(math.log(nkib*1024)/math.log(2)).toInt


  //DebugPort

  if(debug){
    val probe_addr = Wire(UInt(realWidth.W))
    probe_addr := 0.U
    probe_addr := io.ProbePort.get.addr(realWidth-1,0)
    io.ProbePort.get.data := content(probe_addr)
  }

  //IF read
  val valid_IF_addr = Mux(io.IFPort.addr(1,0) === 0.U && io.IFPort.addr +3.U <= (nkib*1024).U, io.IFPort.addr(realWidth-1,0),0.U)
  io.IFPort.inst := content(valid_IF_addr +3.U) ##  content(valid_IF_addr + 2.U) ##  content(valid_IF_addr +1.U) ##  content(valid_IF_addr ) //little_endiant
  
  //Mem
  //in this single-cycle RV64I core, just read mem by load, or jusr write in by store
  //don't worry about write-first or write-first, when read and write same addr
  //Mem Read

  //val rword = Cat(Seq.tabulate(8)(i => content(io.MemPort.addr + (7-i).U))) // get 64-bits

  //lack logic about out of range
  /*
  val valid_data = Wire(UInt(64.W))
  val leading_sign = Wire(UInt(64.W))
  leading_sign := Fill(64,io.MemPort.sig)
  valid_data := Cat(leading_sign, Cat(Seq.tabulate(8)(i => content(io.MemPort.addr + (7-i).U)))) // get 64-bits

  switch(io.MemPort.width){
    is("b100".U) {io.MemPort.rdata := leading_sign << 64.U | valid_data} 
    is("b010".U) {io.MemPort.rdata := leading_sign << 32.U | valid_data}
    is("b001".U) {io.MemPort.rdata := leading_sign << 16.U | valid_data}
    is("b000".U) {io.MemPort.rdata := leading_sign <<  8.U | valid_data}
  }*/

 //TODO: no range check
 val valid_mem_addr = Wire(UInt(realWidth.W))
 valid_mem_addr := io.MemPort.addr(realWidth-1,0)
 val word_raw = Cat(Seq.tabulate(8)(i => content(valid_mem_addr + (7-i).U))) //get 64-bits
 val load_sig = Wire(Bool())
 load_sig := false.B
 io.MemPort.rdata := 0.U
  switch(io.MemPort.bfwd){
    is("b100".U){io.MemPort.rdata := word_raw}
    is("b010".U){
      when(io.MemPort.sig){
        load_sig := word_raw(31)
        io.MemPort.rdata := Fill(32,load_sig) ## word_raw(31,0)
      }.otherwise{
        io.MemPort.rdata := Fill(32,false.B) ## word_raw(31,0)
      }
    }
    is("b001".U){
      when(io.MemPort.sig){
        load_sig := word_raw(15)
        io.MemPort.rdata := Fill(64-16,load_sig) ## word_raw(15,0)
      }.otherwise{
        io.MemPort.rdata := Fill(64-16,false.B) ## word_raw(15,0)
      }
      }
    is("b000".U){
      when(io.MemPort.sig){
        load_sig := word_raw(7)
        io.MemPort.rdata := Fill(64-8,load_sig) ## word_raw(7,0)
      }.otherwise{
        io.MemPort.rdata := Fill(64-8,false.B) ## word_raw(7,0)
      }
    }

  }

  //Mem write

  val bytesToWrite = Wire(UInt(4.W))
  bytesToWrite := Mux(io.MemPort.bfwd === "b000".U,"b0001".U,io.MemPort.bfwd ## false.B)
  
  for(i <- 0 until 8){
    when( (i.U < bytesToWrite) && io.MemPort.WE){
      content(valid_mem_addr + i.U) := io.MemPort.wdata((i+1)*8-1, i*8)
    }
  }

}
import _root_.circt.stage.ChiselStage
object GenerateMem extends App {
  ChiselStage.emitSystemVerilogFile(
    new MainMem(1)()(),
    Array("--target-dir", "build/Mem"),
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}
