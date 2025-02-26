package core.ALU

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers


import scala.io.Source
import java.io.File


// 测试用例类
case class ALUTestCase(a: BigInt, b: BigInt, op: Int, expected: BigInt)

class ALUSpec extends AnyFreeSpec with Matchers {
  
  // 解析16进制字符串为BigInt
  def parseHexString(hexStr: String): BigInt = BigInt(hexStr.stripPrefix("0x"), 16)
  
  // 从CSV文件读取测试用例
  def loadTestCases(filename: String): Seq[ALUTestCase] = {
    val source = Source.fromFile(filename)
    val testCases = source.getLines().drop(1) // 跳过标题行
      .map { line =>
        val fields = line.split(",")
        ALUTestCase(
          a = parseHexString(fields(0)),
          b = parseHexString(fields(1)),
          op = fields(2).toInt,
          expected = parseHexString(fields(3))
        )
      }.toSeq
    source.close()
    testCases
  }
  
  "ALU should pass all test cases from CSV" in {
    
    simulate(new ALU) { dut =>
      val testCases = loadTestCases("unittest/ALU/alu_testcases.csv")
      for (testCase <- testCases) {
        dut.io.op.poke(testCase.op.U)
        dut.io.src1.poke(testCase.a.U)
        dut.io.src2.poke(testCase.b.U)
        dut.io.out.expect(testCase.expected.U)
      }
      }
    }
  }

class ALU_ADDSpec extends AnyFreeSpec with Matchers {
  "test 0+1=1" in {
    simulate(new ALU) { dut =>
      dut.io.op.poke(ALUOp.OP_ADD)
      dut.io.src1.poke(0.U)
      dut.io.src2.poke(1.U)
      dut.io.out.expect(1.U)
    }
  }

  "test small int addition" in {
    simulate(new ALU) { dut =>
      dut.io.op.poke(ALUOp.OP_ADD)
      for( src1 <- 0 to 20){
        for(src2 <- 0 to 52) {
          dut.io.src1.poke(src1.U)
          dut.io.src2.poke(src2.U)
          dut.io.out.expect((src1 + src2).U)
        }
      }
    }
  }

  "test add with big step" in {
    simulate(new ALU) { dut =>
      dut.io.op.poke(ALUOp.OP_ADD)
      val data=Array[BigInt](1,2,3,0xFF,0xFFF,0xFFFF,BigInt("FFFFFFFFFFFFF00",16),BigInt("FFFFFFFFFFFFFF0",16),BigInt("FFFFFFFFFFFFFFF",16))
      for(src1 <- data){
        for( src2 <- 0 to 8 by 2){
          dut.io.src1.poke(src1.U(64.W))
          dut.io.src2.poke(src2.U(64.W))
          dut.io.out.expect( (( src1+src2 )&BigInt("FFFFFFFFFFFFFFFF",16) ).U(64.W) )
        }
      }
    }
  }

}


class ALU_ANDSpec extends AnyFreeSpec with Matchers {
  "test 0&1=0" in {
    simulate(new ALU) { dut =>
      dut.io.op.poke(ALUOp.OP_AND)
      dut.io.src1.poke(0.U)
      dut.io.src2.poke(1.U)
      dut.io.out.expect(0.U)
    }
  }

  "test small int addition" in {
    simulate(new ALU) { dut =>
      dut.io.op.poke(ALUOp.OP_AND)
      for( src1 <- 0 to 20){
        for(src2 <- 0 to 52) {
          dut.io.src1.poke(src1.U)
          dut.io.src2.poke(src2.U)
          dut.io.out.expect((src1 & src2).U)
        }
      }
    }
  }

  "test add with big step" in {
    simulate(new ALU) { dut =>
      dut.io.op.poke(ALUOp.OP_AND)
      val data=Array[BigInt](1,2,3,0xFF,0xFFF,0xFFFF,BigInt("FFFFFFFFFFFFF00",16),BigInt("FFFFFFFFFFFFFF0",16),BigInt("FFFFFFFFFFFFFFF",16))
      for(src1 <- data){
        for( src2 <- 0 to 8 by 2){
          dut.io.src1.poke(src1.U(64.W))
          dut.io.src2.poke(src2.U(64.W))
          dut.io.out.expect( (( src1&src2 )&BigInt("FFFFFFFFFFFFFFFF",16) ).U(64.W) )
        }
      }
    }
  }


}

