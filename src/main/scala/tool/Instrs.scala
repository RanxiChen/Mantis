package tool


object Instrs {
  val ISA:Map[String,(BigInt,String)] = Map(
  // Loads
"lb" -> (BigInt("00000000000000000000000000000011",2), "i"),
"lh" -> (BigInt("00000000000000000001000000000011",2), "i"),
"lw" -> (BigInt("00000000000000000010000000000011",2), "i"),
"lbu" -> (BigInt("00000000000000000100000000000011",2), "i"),
"lhu" -> (BigInt("00000000000000000101000000000011",2), "i"),
"lwu" -> (BigInt("00000000000000000110000000000011",2), "i"),
"ld" -> (BigInt("00000000000000000011000000000011",2), "i"),

// Stores
"sb" -> (BigInt("00000000000000000000000000100011",2), "s"),
"sh" -> (BigInt("00000000000000000001000000100011",2), "s"),
"sw" -> (BigInt("00000000000000000010000000100011",2), "s"),
"sd" -> (BigInt("00000000000000000011000000100011",2), "s"),

// Shifts
"sll" -> (BigInt("00000000000000000001000000110011",2), "r"),
"slli" -> (BigInt("00000000000000000001000000010011",2), "i"),
"srl" -> (BigInt("00000000000000000101000000110011",2), "r"),
"srli" -> (BigInt("00000000000000000101000000010011",2), "i"),
"sra" -> (BigInt("01000000000000000101000000110011",2), "r"),
"srai" -> (BigInt("01000000000000000101000000010011",2), "i"),
// Arithmetic
"add" -> (BigInt("00000000000000000000000000110011",2), "r"),
"addi" -> (BigInt("00000000000000000000000000010011",2), "i"),
"sub" -> (BigInt("01000000000000000000000000110011",2), "r"),
"lui" -> (BigInt("00000000000000000000000000110111",2), "u"),
"auipc" -> (BigInt("00000000000000000000000000010111",2), "u"),
// Logical
"xor" -> (BigInt("00000000000000000100000000110011",2), "r"),
"xori" -> (BigInt("00000000000000000100000000010011",2), "i"),
"or" -> (BigInt("00000000000000000110000000110011",2), "r"),
"ori" -> (BigInt("00000000000000000110000000010011",2), "i"),
"and" -> (BigInt("00000000000000000111000000110011",2), "r"),
"andi" -> (BigInt("00000000000000000111000000010011",2), "i"),
// Compare
"slt" -> (BigInt("00000000000000000010000000110011",2), "r"),
"slti" -> (BigInt("00000000000000000010000000010011",2), "i"),
"sltu" -> (BigInt("00000000000000000011000000110011",2), "r"),
"sltiu" -> (BigInt("00000000000000000011000000010011",2), "i"),
// Branches
"beq" -> (BigInt("00000000000000000000000001100011",2), "b"),
"bne" -> (BigInt("00000000000000000001000001100011",2), "b"),
"blt" -> (BigInt("00000000000000000100000001100011",2), "b"),
"bge" -> (BigInt("00000000000000000101000001100011",2), "b"),
"bltu" -> (BigInt("00000000000000000110000001100011",2), "b"),
"bgeu" -> (BigInt("00000000000000000111000001100011",2), "b"),
// Jump & Link
"jal" -> (BigInt("00000000000000000000000001101111",2), "j"),
"jalr" -> (BigInt("00000000000000000000000001100111",2), "i")
)
}
