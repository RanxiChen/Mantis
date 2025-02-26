package core.CtrlU

import chisel3._

/**
 * Control Unit for RVCORE
 */
class CtrlU extends Module {
  val io = IO(new Bundle {
    val op7 = Input(UInt(7.W))
    val func3 = Input(UInt(3.W))
    val func7 = Input(UInt(7.W))
    //OutPut
    val alu_op = Output(UInt(4.W))
  })

  val alu_op = Wire(UInt(4.W))
  alu_op := 15.U
  
  when(io.op7 === "b0110011".U){
    when(io.func3 === "b000".U){
      when(io.func7 === "b0000000".U){
        alu_op := 0.U
      }
    }
  }
}

