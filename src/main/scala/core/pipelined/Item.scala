package core.pipelined
import chisel3._
import chisel3.util._
import core.Mem.MainMem

class TinySOC(main_memsize:Int,rom_path:String,hexrommapfile:String) extends Module{
    val io = ???
    val main_mem = Module(new MainMem(main_memsize)(rom_path,hexrommapfile)(false))
    val piplinedPU = Module(new PiplinedPU) 
    piplinedPU.io.fetchinst <> main_mem.io.IFPort
    println("Successfully connect fetch to main memory")
}