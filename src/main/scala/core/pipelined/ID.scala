package core.pipelined

import chisel3._
import chisel3.util._
import core.Instructions._
object PipelinedInstDecoder {
    val instrMap = Array(
        ADDI -> List(Reg)
    )
}