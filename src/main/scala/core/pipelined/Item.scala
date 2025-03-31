package core.pipelined
import chisel3._
import chisel3.util._
import core.Mem.MainMem

class TinySOC(main_memsize:Int,rom_path:String ="misc/Mem/rom.hex" ,hexrommapfile:String ="" )(debug:Boolean=false) extends Module{
    val io = IO(new PUProbe)
    val main_mem = Module(new MainMem(main_memsize)(rom_path,hexrommapfile)(false))
    val piplinedPU = Module(new PiplinedPU) 
    piplinedPU.io.fetchinst <> main_mem.io.IFPort
    println("Successfully connect fetch to main memory")
    piplinedPU.io.fetchdata <> main_mem.io.MemPort
    println("Successfully connect fetch to main memory")
    io <> piplinedPU.probe
}