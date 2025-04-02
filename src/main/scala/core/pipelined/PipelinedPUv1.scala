package core.pipelined

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._

class BoundryReg[T <: Bundle] (initialValue:T,enable:Bool,clear:Bool) {
    def |>>>: (consumer:T) = {
        val reg = RegInit(initialValue)
        when(clear){
            reg <> initialValue
        }.elsewhen(enable){
            reg <> consumer
        }
        reg        
    }
}

class adata extends Bundle{
    val a1 = Bool()
    val a2 = UInt(3.W)
}

class a extends Module {
    val io = IO(new Bundle{
        val in = Bool()
        val out = Bool()
    })
    val innerReg = BoundryReg(false.B,true.B,false.B)
    io.in |>>>: innerReg
    io.out <> innerReg
}