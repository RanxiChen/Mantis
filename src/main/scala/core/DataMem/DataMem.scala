package core.DataMem

import chisel3._

/*
 * This Module will used to represent main memory
 * in very early stage
 */
class DataMem extends Module{
  val io=IO(new Bundle{
    val addr=Input(UInt(64.W))
    val write_data=Input(UInt(64.W))
    val read_data=Output(UInt(64.W))
    val WnR=Input(Bool())//true for write false for read
  })
  val data_mem=Mem(1024,UInt(64.W))
  io.read_data:=data_mem(io.addr)
  when(io.WnR){
    data_mem(io.addr):=io.write_data
  }
}
