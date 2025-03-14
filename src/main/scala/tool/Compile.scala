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
    def addimmI(imm:Int):BigInt={
        require((imm >= -2048) && (imm <= 2047))
        if(imm >=0){
            imm << 20
        }else{
            BigInt(imm.toHexString.takeRight(3),16) << 20
        }
    }

    def addimmU(imm:Int):BigInt ={
      val immu = imm &0xFFFFF000
      BigInt(immu) << 12
    }

    def addimmS(imm:Int):BigInt ={
      val inst31 = imm & 0xFFFFF800
      var bit31 = 0
      if(inst31 == 0xFFFFF800){
        bit31 = 1
      }else if(inst31 == 0){
        bit31 = 0
      }else{
        throw new Exception("addimmS error")
      }

      val bit3025 = ( imm & 0x7E0 ) >>> 5
      val bit1108 = ( imm & 0x1E) >>> 1
      val bit7 = imm & 0x1
      bit31 << 31 | bit3025 << 25 | bit1108 << 8 | bit7 << 7
    }

    def addimmB(imm:Int):BigInt={
      require((imm & 0x1) == 0)
      val inst31 = imm & 0xFFFFF000
      var bit31 = 0
      if(inst31 == 0xFFFFF000){
        bit31 = 1
      }else if(inst31 == 0){
        bit31 = 0
      }else{
        throw new Exception("addimmB error")
      }

      val bit3025 = ( imm & 0x7E0 ) >>> 5
      val bit1108 = ( imm & 0x1E) >>> 1
      val bit7 = (imm & 0xFFFFF800) >>> 11
      bit31 << 31 | bit3025 << 25 | bit7 << 7 | bit1108 << 8
    }

    def addimmJ(imm:Int):BigInt={
      require((imm & 0x1) == 0)
      val inst31 = imm & 0xFFF00000
      var bit31 = 0
      if(inst31 == 0xFFF00000){
        bit31 = 1
      } else if(inst31 == 0){
        bit31 = 0
      }else{
        throw new Exception("addimmJ error")
      }
      val bit1912 = ( imm & 0x000FF000 ) >>> 12
      val bit20 = (imm & 0x00000800) >> 11
      val bit3025 = (imm&0x000007E0) >> 5
      val bit2421 = (imm & 0x0000001E) >> 1
      bit31 << 31 | bit1912 << 12 | bit20 << 20 | bit3025 << 25 | bit2421 << 21
    }
    import tool.Instrs.ISA

    def CC(inst:String,rd:Int =0,rs1:Int =0,rs2:Int =0,imm:Int=0)={
      val (inst_base,inst_type) = ISA(inst)
      inst_base + addrd(rd) + addsrc1(rs1) + addsrc2(rs2) + (
        inst_type match {
          case "i" => addimmI(imm)
          case "s" => addimmS(imm)
          case "b" => addimmB(imm)
          case "u" => addimmU(imm)
          case  _  => 0
        }
        )
    }
}
