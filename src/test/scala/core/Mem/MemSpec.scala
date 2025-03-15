package core.Mem

import chisel3._
import chisel3.util._

import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class MemProbePortReadSpec extends AnyFreeSpec with Matchers {
  "test read by ProbePort" in {
    tool.HexProcess.generateSeqHex()
    simulate(new MainMem(1)(HexMapFile = "misc/Mem/rom.map")(true)) { dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      dut.io.IFPort.addr.poke(0.U)
      dut.io.MemPort.addr.poke(0.U)
      dut.io.MemPort.WE.poke(false.B)
      //dut.io.ProbePort.get.addr.poke(1.U)
      //println(dut.io.ProbePort.get.data.peek().litValue)
      //dut.io.ProbePort.get.addr.poke(1023.U)
      //println(dut.io.ProbePort.get.data.peek().litValue)
      for( i <- 0 until 256){
        dut.io.ProbePort.get.addr.poke(i.U)
        dut.io.ProbePort.get.data.expect(i.U)
        //println(s"This ${i}-th location is ${dut.io.ProbePort.get.data.peek().litValue}")
      }
    }
  }
}
class MemIFPortReadSpec extends AnyFreeSpec with Matchers {
  "test read by IFPort" in {
    tool.HexProcess.generateSeqHex()
    simulate(new MainMem(1)()(true)) { dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      //32 bits read
      val HexfilePath = os.pwd/"misc/Mem/rom.hex"
      val raw_hex_Seq = os.read.lines(HexfilePath)
      var ifaddr=0
      for(line <- raw_hex_Seq){
        dut.io.IFPort.addr.poke(ifaddr.U)
        //println(s"data:${BigInt(line,16)} at addr:${ifaddr} store ${dut.io.IFPort.inst.peek().litValue}")
        dut.io.IFPort.inst.expect(BigInt(line,16).U)
        ifaddr += 4
      }
  }
}
}

class MemMemPortReadNoSigSpec extends AnyFreeSpec with Matchers {
  "test read by MemPort without sig" in {
    tool.HexProcess.generateSeqHex()
    simulate(new MainMem(1)()(true)) { dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      //32 bits hex file
      val HexfilePath = os.pwd/"misc/Mem/rom.hex"
      dut.io.MemPort.WE.poke(false.B)
      //just byte
      dut.io.MemPort.bfwd.poke("b000".U)
      //no sig extends
      dut.io.MemPort.sig.poke(false.B)
      val testCase=List(0x1,0x5,0xf,0x10,0x15,0x1a,0x5f,0x7a,0xf2)
      for(testcase <- testCase){
        dut.io.MemPort.addr.poke(testcase.U)
        //println(s"data:${testcase} at addr:${testcase} store ${dut.io.MemPort.rdata.peek().litValue}")
        dut.io.MemPort.rdata.expect(testcase.U)
      }
      //half,still no sig extends
      dut.io.MemPort.bfwd.poke("b001".U)
      val testHalfCase=List(0x1,0x5,0xf,0x10,0x15,0x1a,0x5f,0x7a,0xf2)
      def refValueHalf(n:Int):BigInt={
        ( (n +1) << 8) | n
      }
      //val halfresBar=List(0x0201,0x0605,0x100f,0x1110,0x1615,0x1b1a,0x605f,0x7b7a,0xf3f2)
      val halfresBar=testHalfCase.map(refValueHalf)
      for(testcase <- (testHalfCase zip halfresBar)){
        dut.io.MemPort.addr.poke(testcase._1.U)
        //println(s"using testcase:${testcase}, get data:${(dut.io.MemPort.rdata.peek().litValue)}")
        dut.io.MemPort.rdata.expect(testcase._2.U)
      }
      val testWordCase=List(0x1,0x5,0xf,0x10,0x15,0x1a,0x5f,0x7a,0xf2)
      def refValueWord(i:Int):BigInt={
        val n = BigInt(i)
        (n+3) << 24 | (n+2) << 16 | (n+1) << 8 | n
      }
      val wordrefBar = testWordCase.map(refValueWord)
      dut.io.MemPort.bfwd.poke("b010".U)
      for(testcase <- (testWordCase zip wordrefBar)){
        dut.io.MemPort.addr.poke(testcase._1.U)
        //println(s"using testcase:${testcase}, get data:${(dut.io.MemPort.rdata.peek().litValue)}")
        dut.io.MemPort.rdata.expect(testcase._2.U)
      }
      val testDoubleCase=List(0x1,0x5,0xf,0x10,0x15,0x1a,0x5f,0x7a,0xf2)
      def refValueDouble(i:Int):BigInt={
        val n = BigInt(i)
        (n+7) << 56 | (n+6) << 48 | (n+5) << 40 | (n+4) << 32 | (n+3) << 24 | (n+2) << 16 | (n+1) << 8 | n
      }
      val doublerefBar = testDoubleCase.map(refValueDouble)
      dut.io.MemPort.bfwd.poke("b100".U)
      for(testcase <- (testDoubleCase zip doublerefBar)){
        dut.io.MemPort.addr.poke(testcase._1.U)
        //println(s"using testcase:${testcase}, get data:${(dut.io.MemPort.rdata.peek().litValue.toString(16))}")
        dut.io.MemPort.rdata.expect(testcase._2.U)
      }
    }
  }
}
/*
class MemMemPortReadSigSpec extends AnyFreeSpec with Matchers {
  "test read by MemPort with sig" in {
    simulate(new MainMem(1)()(true)) { dut =>
      //reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.reset.poke(false.B)
      //32 bits hex file
      val HexfilePath = os.pwd/"misc/Mem/rom.hex"
      dut.io.MemPort.WE.poke(false.B)
      //just byte
      dut.io.MemPort.bfwd.poke("b000".U)
      //no sig extends
      dut.io.MemPort.sig.poke(false.B)
      val testCase=List(0x1,0x5,0xf,0x10,0x15,0x1a,0x5f,0x7a,0x81,0x92,0xa5,0xa7,0xf2)
      def bytesigbit(i:Int):Boolean={
        (i & 0x80) == 0x80
      }
      def sigbyteref(i:Int):BigInt={
        if(bytesigbit(i)){
          BigInt("FFFFFFFFFFFFFF00",16) + i 
        }else{
          i
        }
      }
      for(testcase <- testCase zip testCase.map(sigbyteref)){
        dut.io.MemPort.addr.poke(testcase._1.U)
        println(s"data:${(testcase._2).toString(16)}  store ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        //dut.io.MemPort.rdata.expect(testcase._2.U)
      }
      //half,still no sig extends
      dut.io.MemPort.bfwd.poke("b001".U)
      val testHalfCase=List(0x1,0x5,0xf,0x10,0x15,0x1a,0x5f,0x7a,0xf2)
      def refValueHalf(n:Int):BigInt={
        if(((n+1)&0x80) == 0x80){
          BigInt("FFFFFFFFFFFF0000",16) | (n+1) << 8 | n
        }else{
          ( (n +1) << 8) | n
        }
      }
      //val halfresBar=List(0x0201,0x0605,0x100f,0x1110,0x1615,0x1b1a,0x605f,0x7b7a,0xf3f2)
      val halfresBar=testHalfCase.map(refValueHalf)
      for(testcase <- (testHalfCase zip halfresBar)){
        dut.io.MemPort.addr.poke(testcase._1.U)
        println(s"using testcase:${testcase._2.toString(16)}, get data:${(dut.io.MemPort.rdata.peek().litValue.toString(16))}")
        //dut.io.MemPort.rdata.expect(testcase._2.U)
      }
      val testWordCase=List(0x1,0x5,0xf,0x10,0x15,0x1a,0x5f,0x7a,0xf2)
      def refValueWord(i:Int):BigInt={
        if(((i+3)&0x80) == 0x80){
          BigInt("FFFFFFFF00000000",16) | (i+3) << 24 | (i+2) << 16 | (i+1) << 8 | i
        }else{
          (i+3) << 24 | (i+2) << 16 | (i+1) << 8 | i
        }
      }
      val wordrefBar = testWordCase.map(refValueWord)
      dut.io.MemPort.bfwd.poke("b010".U)
      for(testcase <- (testWordCase zip wordrefBar)){
        dut.io.MemPort.addr.poke(testcase._1.U)
        println(s"using testcase:${testcase._2.toString(16)}, get data:${(dut.io.MemPort.rdata.peek().litValue.toString(16))}")
        //dut.io.MemPort.rdata.expect(testcase._2.U)
      }
      val testDoubleCase=List(0x1,0x5,0xf,0x10,0x15,0x1a,0x5f,0x7a,0xf2)
      def refValueDouble(i:Int):BigInt={
        val n = BigInt(i)
        (n+7) << 56 | (n+6) << 48 | (n+5) << 40 | (n+4) << 32 | (n+3) << 24 | (n+2) << 16 | (n+1) << 8 | n
      }
      val doublerefBar = testDoubleCase.map(refValueDouble)
      dut.io.MemPort.bfwd.poke("b100".U)
      for(testcase <- (testDoubleCase zip doublerefBar)){
        dut.io.MemPort.addr.poke(testcase._1.U)
        //println(s"using testcase:${testcase}, get data:${(dut.io.MemPort.rdata.peek().litValue.toString(16))}")
        dut.io.MemPort.rdata.expect(testcase._2.U)
      }
    }
  }
}
*/


class MemPortReadWriteSpec extends AnyFreeSpec with Matchers {
  tool.HexProcess.generateSeqHex()
  
  "MemPort should correctly handle reads and writes" - {
    
    "test unsigned read operations (sig = false)" in {
      simulate(new MainMem(1)()(true)) { dut =>
        // Reset circuit
        dut.reset.poke(true.B)
        dut.clock.step()
        dut.reset.poke(false.B)
        
        // Setup for reading
        dut.io.MemPort.WE.poke(false.B)
        dut.io.MemPort.sig.poke(false.B)
        
        // Test word reads (32-bit, bfwd = "010")
        dut.io.MemPort.bfwd.poke("b010".U)
        val testCases = List(0x0, 0x4, 0x8, 0xC)
        val wordResults = List(0x03020100L, 0x07060504L, 0x0B0A0908L, 0x0F0E0D0CL)
        
        for ((addr, expected) <- testCases zip wordResults) {
          dut.io.MemPort.addr.poke(addr.U)
          dut.io.MemPort.rdata.expect(expected.U)
          //println(s"Word read at 0x${addr.toHexString}: expected 0x${expected.toHexString}, got ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        }
        
        // Test double reads (64-bit, bfwd = "100")
        dut.io.MemPort.bfwd.poke("b100".U)
        val doubleResults = List(0x0706050403020100L, 0x0F0E0D0C0B0A0908L)
        
        for ((addr, expected) <- List(0x0, 0x8) zip doubleResults) {
          dut.io.MemPort.addr.poke(addr.U)
          dut.io.MemPort.rdata.expect(expected.U)
          //println(s"Double read at 0x${addr.toHexString}: expected 0x${expected.toHexString}, got ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        }
      }
    }
    
    "test signed read operations (sig = true)" in {
      simulate(new MainMem(1)()(true)) { dut =>
        // Reset circuit
        dut.reset.poke(true.B)
        dut.clock.step()
        dut.reset.poke(false.B)
        
        // Setup for reading with sign extension
        dut.io.MemPort.WE.poke(false.B)
        dut.io.MemPort.sig.poke(true.B)
        
        // Test byte reads with sign extension (bfwd = "000")
        dut.io.MemPort.bfwd.poke("b000".U)
        val byteSignedTestCases = List(0x7F, 0x80, 0xFE).map(BigInt(_))
        
        // 0x7F has top bit 0, so no sign extension
        // 0x80 and 0xFE have top bit 1, so sign extend with 1s
        // Using hex representation for the expected results to avoid negative number issues
        val byteSignedResults = List(
          "000000000000007F",     // 0x7F (positive, no sign extension)
          "FFFFFFFFFFFFFF80",     // 0x80 (top bit 1, sign extension with 1s)
          "FFFFFFFFFFFFFFFE"      // 0xFE (top bit 1, sign extension with 1s)
        ).map(BigInt(_,16))
        
        for ((addr, expected) <- byteSignedTestCases zip byteSignedResults) {
          dut.io.MemPort.addr.poke(addr.U)
          // 使用十六进制字面量避免负数问题
          dut.io.MemPort.rdata.expect(expected.U)
          //println(s"Signed byte read at 0x${addr.toString(16)}: expected 0x${expected.toString(16)}, got ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        }
        
        // Test half reads with sign extension (bfwd = "001")
        dut.io.MemPort.bfwd.poke("b001".U)
        val halfSignedTestCases = List(0x7E, 0x80).map(BigInt(_))
        
        // At address 0x7E we have bytes 0x7F, 0x7E forming halfword 0x7E7F (top bit 0)
        // At address 0x80 we have bytes 0x81, 0x80 forming halfword 0x8081 (top bit 1)
        // Using hex representation for expected results
        val halfSignedResults = List(
          "0000000000007F7E",  // 0x7E7F (top bit 0, no sign extension)
          "FFFFFFFFFFFF8180"   // 0x8081 (top bit 1, sign extension with 1s)
        ).map(BigInt(_,16))
        
        for ((addr, expected) <- halfSignedTestCases zip halfSignedResults) {
          dut.io.MemPort.addr.poke(addr.U)
          dut.io.MemPort.rdata.expect(expected.U)
          //println(s"Signed half read at 0x${addr.toString(16)}: expected 0x${expected.toString(16)}, got ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        }
        
        // Test word reads with sign extension (bfwd = "010")
        dut.io.MemPort.bfwd.poke("b010".U)
        val wordSignedTestCases = List(0x7C, 0x80)
        
        // At address 0x7C we have bytes 0x7F, 0x7E, 0x7D, 0x7C  (top bit 0)
        // At address 0x80 we have bytes 0x83, 0x82, 0x81, 0x80  (top bit 1)
        // Using hex representation for expected results
        val wordSignedResults = List(
          BigInt("000000007F7E7D7C",16),  //  (top bit 0, no sign extension)
          BigInt("FFFFFFFF83828180",16)   //  (top bit 1, sign extension with 1s)
        )
        
        for ((addr, expected) <- wordSignedTestCases zip wordSignedResults) {
          dut.io.MemPort.addr.poke(addr.U)
          dut.io.MemPort.rdata.expect(expected.U)
          //println(s"Signed word read at 0x${addr.toHexString}: expected 0x${expected.toString(16)}, got ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        }
        
        // Double reads don't need sign extension testing since they're already 64-bit
      }
    }
    
    "test write operations for all sizes" in {
      simulate(new MainMem(1)()(true)) { dut =>
        // Reset circuit
        dut.reset.poke(true.B)
        dut.clock.step()
        dut.reset.poke(false.B)
        
        // First read some values to confirm initial state
        dut.io.MemPort.WE.poke(false.B)
        dut.io.MemPort.sig.poke(false.B)
        dut.io.MemPort.bfwd.poke("b100".U)
        
        val testAddr = BigInt("100",16)  // Using an address away from initialized data
        dut.io.MemPort.addr.poke(testAddr.U)
        //println(s"Initial value at 0x${testAddr.toString(16)}: ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        
        // Test byte write (bfwd = "000")
        dut.io.MemPort.WE.poke(true.B)
        dut.io.MemPort.bfwd.poke("b000".U)
        val byteTestData = 0xABL
        dut.io.MemPort.wdata.poke(byteTestData.U)
        dut.io.MemPort.addr.poke(testAddr.U)
        dut.clock.step()
        
        // Verify byte write
        dut.io.MemPort.WE.poke(false.B)
        dut.io.MemPort.bfwd.poke("b000".U)
        dut.io.MemPort.addr.poke(testAddr.U)
        dut.io.MemPort.rdata.expect(byteTestData.U)
        //println(s"After byte write: expected 0x${byteTestData.toHexString}, got ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        
        // Test half write (bfwd = "001")
        dut.io.MemPort.WE.poke(true.B)
        dut.io.MemPort.bfwd.poke("b001".U)
        val halfTestData = 0xBEEFL
        dut.io.MemPort.wdata.poke(halfTestData.U)
        dut.io.MemPort.addr.poke((testAddr + 2).U)
        dut.clock.step()
        
        // Verify half write
        dut.io.MemPort.WE.poke(false.B)
        dut.io.MemPort.bfwd.poke("b001".U)
        dut.io.MemPort.addr.poke((testAddr + 2).U)
        dut.io.MemPort.rdata.expect(halfTestData.U)
        //println(s"After half write: expected 0x${halfTestData.toHexString}, got ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        
        // Test word write (bfwd = "010")
        dut.io.MemPort.WE.poke(true.B)
        dut.io.MemPort.bfwd.poke("b010".U)
        val wordTestData = 0xCAFEBABEL
        dut.io.MemPort.wdata.poke(wordTestData.U)
        dut.io.MemPort.addr.poke((testAddr + 4).U)
        dut.clock.step()
        
        // Verify word write
        dut.io.MemPort.WE.poke(false.B)
        dut.io.MemPort.bfwd.poke("b010".U)
        dut.io.MemPort.addr.poke((testAddr + 4).U)
        dut.io.MemPort.rdata.expect(wordTestData.U)
        //println(s"After word write: expected 0x${wordTestData.toHexString}, got ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        
        // Test double write (bfwd = "100")
        dut.io.MemPort.WE.poke(true.B)
        dut.io.MemPort.bfwd.poke("b100".U)
        // 使用正数十六进制表示，避免负数问题
        val doubleTestData = BigInt("DEADBEEFCAFEBABE", 16)
        dut.io.MemPort.wdata.poke(doubleTestData.U)
        dut.io.MemPort.addr.poke((testAddr + 8).U)
        dut.clock.step()
        
        // Verify double write
        dut.io.MemPort.WE.poke(false.B)
        dut.io.MemPort.bfwd.poke("b100".U)
        dut.io.MemPort.addr.poke((testAddr + 8).U)
        dut.io.MemPort.rdata.expect(doubleTestData.U)
        //println(s"After double write: expected 0x${doubleTestData.toString(16)}, got ${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        
        // Verify all writes together using ProbePort for byte-by-byte verification
        //println("Verifying individual bytes through ProbePort:")
        
        // Check byte write
        dut.io.ProbePort.get.addr.poke(testAddr.U)
        //println(s"Byte at addr 0x${testAddr.toString(16)}: 0x${dut.io.ProbePort.get.data.peek().litValue.toString(16)}, expected: 0x${(byteTestData & 0xFF).toHexString}")
        
        // Check half write (2 bytes)
        //println("Half write bytes:")
        for (i <- 0 until 2) {
          val addr = testAddr + 2 + i
          dut.io.ProbePort.get.addr.poke(addr.U)
          val expected = (halfTestData >> (i * 8)) & 0xFF
          //println(s"Byte at addr 0x${addr.toString(16)}: 0x${dut.io.ProbePort.get.data.peek().litValue.toString(16)}, expected: 0x${expected.toHexString}")
        }
        
        // Check word write (4 bytes)
        //println("Word write bytes:")
        for (i <- 0 until 4) {
          val addr = testAddr + 4 + i
          dut.io.ProbePort.get.addr.poke(addr.U)
          val expected = (wordTestData >> (i * 8)) & 0xFF
          //println(s"Byte at addr 0x${addr.toString(16)}: 0x${dut.io.ProbePort.get.data.peek().litValue.toString(16)}, expected: 0x${expected.toHexString}")
        }
        
        // Check double write (8 bytes)
        //println("Double write bytes:")
        for (i <- 0 until 8) {
          val addr = testAddr + 8 + i
          dut.io.ProbePort.get.addr.poke(addr.U)
          val expected = (doubleTestData >> (i * 8)) & 0xFF
          //println(s"Byte at addr 0x${addr.toString(16)}: 0x${dut.io.ProbePort.get.data.peek().litValue.toString(16)}, expected: 0x${expected.toString(16)}")
        }
      }
    }
    
    "test boundary conditions and endianness" in {
      simulate(new MainMem(1)()(true)) { dut =>
        // Reset circuit
        dut.reset.poke(true.B)
        dut.clock.step()
        dut.reset.poke(false.B)
        
        // Test writing a pattern that clearly shows endianness
        val testAddr = 0x200
        dut.io.MemPort.WE.poke(true.B)
        dut.io.MemPort.bfwd.poke("b100".U)
        dut.io.MemPort.wdata.poke(0x0102030405060708L.U)
        dut.io.MemPort.addr.poke(testAddr.U)
        dut.clock.step()
        
        // Read back with different widths to verify endianness
        dut.io.MemPort.WE.poke(false.B)
        dut.io.MemPort.sig.poke(false.B)
        
        // Check double read (should be the full pattern)
        dut.io.MemPort.bfwd.poke("b100".U)
        dut.io.MemPort.addr.poke(testAddr.U)
        dut.io.MemPort.rdata.expect(BigInt("0102030405060708",16).U)
        //println(s"Double read: 0x${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        
        // Check word reads
        dut.io.MemPort.bfwd.poke("b010".U)
        dut.io.MemPort.addr.poke(testAddr.U)
        dut.io.MemPort.rdata.expect(0x05060708L.U)
        //println(s"Word read at offset 0: 0x${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        
        dut.io.MemPort.addr.poke((testAddr + 4).U)
        dut.io.MemPort.rdata.expect(0x01020304L.U)
        //println(s"Word read at offset 4: 0x${dut.io.MemPort.rdata.peek().litValue.toString(16)}")
        
        // Check half reads
        dut.io.MemPort.bfwd.poke("b001".U)
        for (i <- 0 until 4) {
          val addr = testAddr + i * 2
          dut.io.MemPort.addr.poke(addr.U)
          val expected = ((BigInt("0102030405060708",16) >> (i * 16)) & 0xFFFF)
          dut.io.MemPort.rdata.expect(expected.U)
          //println(s"Half read at offset ${i*2}: 0x${dut.io.MemPort.rdata.peek().litValue.toString(16)}, expected: 0x${expected.toString(16)}")
        }
        
        // Check byte reads
        dut.io.MemPort.bfwd.poke("b000".U)
        for (i <- 0 until 8) {
          val addr = testAddr + i
          dut.io.MemPort.addr.poke(addr.U)
          val expected = ((BigInt("0102030405060708",16) >> (i * 8)) & 0xFF)
          dut.io.MemPort.rdata.expect(expected.U)
          //println(s"Byte read at offset $i: 0x${dut.io.MemPort.rdata.peek().litValue.toString(16)}, expected: 0x${expected.toString(16)}")
        }
      }
    }
  }
}

