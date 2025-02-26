package tool

object CC{
    def addrd( rd:Int):BigInt={
        require((rd >=0)&&(rd<=31))
        rd << 7
    }
    def addsrc1(rs1:Int):BigInt={
        require((rs1>=0)&&(rs1<=31))
        rs1 << 15
    }
    def addsrc2(rs2:Int):BigInt={
        require((rs2>=0)&&(rs2<=31))
        rs2 << 20
    }
    def addimm(imm:Int):BigInt={
        require((imm >= -2048) && (imm <= 2047))
        if(imm >=0){
            imm << 20
        }else{
            BigInt(imm.toHexString.takeRight(3),16) << 20
        }
    }
    val ISA:Map[String,BigInt] = Map(
        "add"   -> BigInt("00000000000000000000000000110011",2),
        "sub"   -> BigInt("01000000000000000000000000110011",2),
        "sll"   -> BigInt("00000000000000000001000000110011",2),
        "xor"   -> BigInt("00000000000000000100000000110011",2),
        "srl"   -> BigInt("00000000000000000101000000110011",2),
        "sra"   -> BigInt("01000000000000000101000000110011",2),
        "or"    -> BigInt("00000000000000000110000000110011",2),
        "and"   -> BigInt("00000000000000000111000000110011",2),
        "slt"   -> BigInt("00000000000000000010000000110011",2),
        "sltu"  -> BigInt("00000000000000000011000000110011",2),
        "addi"  -> BigInt("00000000000000000000000000010011",2),
        "xori"  -> BigInt("00000000000000000100000000010011",2),
        "ori"   -> BigInt("00000000000000000110000000010011",2),
        "andi"  -> BigInt("00000000000000000111000000010011",2),
        "slli"  -> BigInt("00000000000000000001000000010011",2),
        "srai"  -> BigInt("01000000000000000101000000010011",2),
        "slti"  -> BigInt("00000000000000000010000000010011",2),
        "sltiu" -> BigInt("00000000000000000011000000010011",2)

    )
    def CCr(insttemplate:String,rd:Int,rs1:Int,rs2:Int):BigInt={
        ISA(insttemplate) + addrd(rd) + addsrc1(rs1) + addsrc2(rs2)
    }
    def CCi(insttemplate:String,rd:Int,rs1:Int,imm:Int):BigInt={
        ISA(insttemplate) + addrd(rd) + addsrc1(rs1) + addimm(imm)
    }
}