
package core.PU

import chisel3._
import chisel3.util._

import core.ALU.ALU
import core.CtrlU.CtrlU
import core.RegFile.RegFile

/**PU for processing unit
 * will be used to generate one "core"
 * I mean , such as one single RV64gc core
 */

/**
 * Very Early version, single cycle,just add
 * and instructions from outside
 * No mem
 */

class ExtInstNoMemPU extends Module{
  val io = IO(new Bundle {
    val inst = Input(UInt(64.W))
    val regValue = Output(Vec(32,UInt(64.W)))
  })
  val fU = Module(new ALU)
  val regFile = Module(new RegFile(true))
  io.regValue := regFile.io.content.get

  //inst 2 regFile
  val src1 = Wire(UInt(5.W))
  val src2 = Wire(UInt(5.W))
  src1 := io.inst(19,15)
  src2 := io.inst(24,20)
  val rd = Wire(UInt(5.W))
  rd := io.inst(11,7)


  regFile.io.rs1_addr := src1
  regFile.io.rs2_addr := src2
  regFile.io.W_enable := true.B
  regFile.io.rd_addr := rd
  regFile.io.rd_data := fU.io.out


  val ctrlU = Module(new CtrlU)
  ctrlU.io.inst := io.inst
  fU.io.op := ctrlU.io.alu_op
  val regoimm = ctrlU.io.regoimm

  val imm = Module(new ImmGen)
  imm.io.raw := io.inst(31,20)
  //connect ALU  
  fU.io.src1 := regFile.io.rs1_data
  fU.io.src2 := Mux(regoimm,regFile.io.rs2_data,imm.io.out)

}

class ImmGen extends Module{
  val io = IO(new Bundle {
    val raw = Input(UInt(12.W))
    val out = Output(UInt(64.W))
  })
  io.out := Fill(52,io.raw(11)) ## io.raw
}




   

