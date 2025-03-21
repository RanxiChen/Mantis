package core

import chisel3._
import chisel3.util._

import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

object WRV64I {
    def sigbits (i:BigInt):Boolean ={
        val bits = i & 0x80000000
        if(bits == 0x80000000) true else false
    }
    def data32to64 (i:BigInt):BigInt ={
        if(sigbits(i)){
            return 0xFFFFFFFF00000000L | i
        }else{
            return i
        }
    }
}

class take32bitsModuleSpec extends AnyFreeSpec with Matchers  {
  "take32bitsModule test" in {
    simulate(new take32bitsModule) { dut =>
        dut.io.in.poke(0x12345678.U)
        dut.io.out.expect(0x0000000012345678L.U)
        dut.io.in.poke(0x80000000.U)
        dut.io.out.expect(0xFFFFFFFF80000000L.U)
    }
  }
}