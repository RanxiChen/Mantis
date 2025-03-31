package core.pipelined

import chisel3._
import chisel3.experimental.BundleLiterals._

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import tool.Colors._
//import chiseltest._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
class TinySocSpec extends AnyFreeSpec with Matchers {
    //accept rom and run
    "test one Tiny Soc but 5 stage pipelined" in {
        val max_cycle=25
        simulate(new TinySOC(1,"unittest/PipelinedRV64/JustCompute_rom.hex")(true) ){ dut =>
            //reset
            dut.reset.poke(true.B)
            dut.clock.step()
            dut.reset.poke(false.B)
            println("Run after reset")
            for(cyclecnt <- 1 to max_cycle){
                //print(" [" + cyclecnt + "] cycles")
                dut.clock.step()
                print(f"[${dut.piplinedPU.probe.mcycle.peek().litValue}%d]")
                var PCBigInt = dut.piplinedPU.ifetchModule.io.out.pc.peek().litValue
                var instBigInt = dut.piplinedPU.ifetchModule.io.out.inst.peek().litValue
                print(f"${colorbar(cyclecnt%5)}[ID] from PC = 0x${PCBigInt}%016x get Inst: 0x${instBigInt}%08x${RESET} \t")
                println()
            }
            println("run ends")
        }
    }
}