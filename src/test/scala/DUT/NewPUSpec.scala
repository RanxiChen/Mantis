package core.pipelined

import chisel3._
import chisel3.experimental.BundleLiterals._

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

//import chiseltest._
import chisel3.simulator.EphemeralSimulator._

class a extends Bundle {
    val a1 = Bool()
    val a2 = UInt(3.W)
}

class aModule extends Module{
    val io = IO(new Bundle{
        val in = Input(Bool())
        val out = Output(new a)
    })
    
}

class aSpec extends AnyFreeSpec with Matchers {
  "a" in {
    val a = new a
    val b = a.Lit(_.a1 -> true.B, _.a2 -> 3.U)
    println(b)
  }
}