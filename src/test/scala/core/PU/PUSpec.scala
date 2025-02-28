package core.PU

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class ExtInstNoMemPUSpecSimple extends AnyFreeSpec with Matchers {
    "Just add 1 to x1" in {
        simulate(new ExtInstNoMemPU){ dut =>
           val inst=BigInt("00110093",16) //x1 = x2 +1
           dut.io.inst.poke(inst.U)
           dut.clock.step()
           dut.io.regValue(1).expect(1.U)           
        }
    }
    "x1 = x2+1,x3=x4+1,x5=x3+x1" in {
        simulate(new ExtInstNoMemPU){ dut =>
           val inst=BigInt("00110093",16) //x1 = x2 +1
           dut.io.inst.poke(inst.U)
           dut.clock.step()
           dut.io.regValue(1).expect(1.U)           
           dut.io.inst.poke(BigInt("00120193",16))
           dut.clock.step()
           dut.io.regValue(3).expect(1.U)
           dut.io.inst.poke(BigInt("1182B3",16).U)
           dut.clock.step()
           dut.io.regValue(5).expect(2.U)
        }
    }
}
class ExtInstNoMemPUSpecCmp extends AnyFreeSpec with Matchers {
  // find existent test cases
  val comPath = os.pwd/"unittest"/"RV64I_part"
  val fileSeq=os.list(comPath)
  val testCase = for(file <- fileSeq if file.last.endsWith(".S")) yield file.last.dropRight(2)
  for (test <- testCase) {
    val instArray = os.read.lines(comPath/s"${test}.inst").map(BigInt(_,16))
    val testres = os.read.lines(comPath/s"${test}.res").map(BigInt(_,16)) //include all regs
    s"Test file $test" in {
      simulate(new ExtInstNoMemPU){ dut =>
        dut.reset.poke(true.B)
        dut.clock.step()
        dut.reset.poke(false.B)
        for(inst <- instArray){
          dut.io.inst.poke(inst.U)
          dut.clock.step()
        }
        for(reg <- 10 to 17){
          dut.io.regValue(reg).expect(testres(reg).U)
        }
      }
    }
  }

}
class ExtInstNoMemPUSpecCmp2 extends AnyFreeSpec with Matchers {
  // find existent test cases
  val comPath = os.pwd/"unittest"/"RV64I_part"
  val fileSeq=os.list(comPath)
  val testCase = for(file <- fileSeq if file.last.endsWith(".S")) yield file.last.dropRight(2)
  "test from file" in {
    simulate(new ExtInstNoMemPU){ dut =>
       for (test <- testCase) {
          dut.reset.poke(true.B)
          dut.clock.step()
          dut.reset.poke(false.B)
          val instArray = os.read.lines(comPath/s"${test}.inst").map(BigInt(_,16))
          val testres = os.read.lines(comPath/s"${test}.res").map(BigInt(_,16)) //include all regs
          for(inst <- instArray){
            dut.io.inst.poke(inst.U)
            dut.clock.step()
          }
          for(reg <- 10 to 17){
           dut.io.regValue(reg).expect(testres(reg).U)
          }
        }
    }
  }

}
class ExtInstNoMemPUCmpSpectemp extends AnyFreeSpec with Matchers {
  "test cmp2" in {
    simulate(new ExtInstNoMemPU){ dut =>
      val instArray = os.read.lines(os.pwd/"unittest"/"RV64I_part"/"cmp2.inst").map(BigInt(_,16))
      val testres = os.read.lines(os.pwd/"unittest" / "RV64I_part" / "cmp2.res").map(BigInt(_,16)) //include all regs
      for(inst <- instArray){
        dut.io.inst.poke(inst.U)
        dut.clock.step()
        println(s"New inst:${inst.toString(16)}")
        for(reg <- 10 to 17){
          println(s"reg ${reg}: ${dut.io.regValue(reg).peek().litValue}")
        }
      }
      for(reg <- 10 to 17){
        dut.io.regValue(reg).expect(testres(reg).U)
      }
    }
  }
}

