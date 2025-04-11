package core.pipelined

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._

class cpu extends Module {
    val fetchInst = IO(new FetchInstIO)
    val mcycle = RegInit(0.U(64.W))
    mcycle := mcycle + 1.U
    val pcGen = RegInit(0.U(64.W))
    val pcGenEN = Wire(Bool())
    //PC module
    val pc_4 = Wire(UInt(64.W))
    when(pcGenEN){
        pcGen := pc_4
    }
    //IF
    fetchInst.addr := pcGen
    val hexinst = Wire(UInt(64.W))
    hexinst := fetchInst.inst
    val instQueue_pc = RegInit(0.U(64.W))
    val instQueue_inst = RegInit(0.U(64.W))
    val instQueue_EN = Wire(Bool())
    when(instQueue_EN){
        instQueue_pc := pcGen
        instQueue_inst := hexinst
    }
        
}

