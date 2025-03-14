package tool

import org.scalatest.freespec.AnyFreeSpec
import tool.CC._
class CCSpec extends AnyFreeSpec {
    "Test CC" in {
      assert(CC("addi",1,0,imm = 10)== BigInt("00a00093",16))
      assert(CC("addi",2,0,imm = 20) == BigInt("01400113",16))
      assert(CC("add",3,1,2) == BigInt("002081b3",16))
      assert(CC("sub",4,3,1) == BigInt("40118233",16))
      assert(CC("addi",1,0,imm = -8) == BigInt("ff800093",16))
      assert(CC("slli",2,1,imm = 0x2) == BigInt("00209113",16))
      assert(CC("srli",3,1,imm = 0x2) == BigInt("0020d193",16))
      assert(CC("srai",4,1,imm = 0x2) == BigInt("4020d213",16))
      assert(CC("bne",0,1,2,imm = 0x30 - 0x28) == BigInt("00209463",16))
      assert(CC("jal",0,imm = 0) == BigInt("0000006f",16))
      assert(CC("sd",rs2 = 2,rs1=1,imm = 0) == BigInt("0020b023",16))
      assert(CC("ld",rd = 3,rs1=1,imm = 0) == BigInt("0000b183",16))
      assert(CC("lb",rd = 4,rs1=1,imm = 0) == BigInt("00008203",16))
      assert(CC("lh",rd = 5,rs1=1,imm = 0) == BigInt("00009283",16))
      assert(CC("lw",rd = 6,rs1=1,imm = 0) == BigInt("0000a303",16))
      assert(CC("lui",rd = 1,imm = 0xabcde)== BigInt("abcde0b7",16))
      assert(CC("blt",rs1=1,rs2=2,imm= 0x84-0x7c) == BigInt("0020c463",16))
      assert(CC("auipc",rd=2,imm= 0x1000) ==BigInt( "01000117",16))
      assert(CC("ori",rd=1,rs1=0,imm = 291)== BigInt("12306093",16))
      assert(CC("xori",rd=3,rs1=1,imm = 85) == BigInt("0550c193",16))
      assert((BigInt("00208223",16) == CC("sb",rs2=2,imm=4,rs1=1)))
      assert((BigInt("00209423",16) == CC("sh",rs2=2,imm=8,rs1=1)))
      assert((BigInt("0020a623",16) == CC("sw",rs2=2,imm=12,rs1=1)))
      assert((BigInt("0020b823",16) == CC("sd",rs2=2,imm=16,rs1=1)))
      assert((BigInt("00008167",16) == CC("jalr",rd=2,imm=0,rs1=1)))
      assert((BigInt("ff602293",16) == CC("slti",rd=5,rs1 =0,imm= -10)))
      assert((BigInt("fe20bc23",16) == CC("sd",rs2=2,imm = -8,rs1=1)))
      assert((BigInt("ff80b183",16) == CC("ld",rd=3,imm = -8,rs1=1)))
      assert((BigInt("fe20ae23",16) == CC("sw",rs2=2,imm = -4,rs1=1)))
      assert((BigInt("ffc0a203",16) == CC("lw",rs2=4,imm = -4,rs1=1)))
      assert((BigInt("fe209f23",16) == CC("sh",rs2=2,imm = -2,rs1=1)))
    }
}
