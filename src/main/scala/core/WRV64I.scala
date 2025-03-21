package core

//This module can pick the lower 32 bits of one 64 bits input
//and signal extends to 64 bits
// W-related instructions
import chisel3._
import chisel3.util._

class take32bitsModule extends Module {
    val io = IO(new Bundle {
        val in = Input(UInt(64.W))
        val out = Output(UInt(64.W))
    })
    io.out := Fill(32, io.in(31)) ## io.in(31, 0)
} 