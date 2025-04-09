package core.pipelined

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._

class BoundaryReg[T <: Data](init:T,EN:Bool,CLR:Bool) {
    val reg = withReset(CLR){RegInit(init)}
    def |>>>: (next: => Data):Unit ={
        when(EN){
            reg := next
        }
    }
}

object BoundaryReg {
    def apply[T <: Data](init:T,EN:Bool,CLR:Bool):T = {
        withReset(CLR){RegInit(init)}
    }
}

