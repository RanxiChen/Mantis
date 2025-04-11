package core

import chisel3._

import chisel3.util._

object IDMap {
  import Signal._
  import Instructions._
  import ALU.ALUOp._
  import BrExe.BrOp._
  val defaultCtrl = 
                  List(PC_4     ,A_XXX   ,B_XXX,   IMM_X  ,OP_XXX,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_XXX   ,WB_N)
  //                     |         |         |        |       |      |          |        |      |           |          |
  //                   pc_sel   alu_src1  alu_src2 imm_sel alu_op    bru_op mem_width  mem_sig   mem_we  wb_sel      wb_en
  val instrMap = Array(
    LUI        -> List(PC_4     ,A_XXX   ,B_XXX,   IMM_U  ,OP_XXX,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_IMM   ,WB_W),
    AUIPC      -> List(PC_4     ,A_PC    ,B_IMM,   IMM_U  ,OP_ADD,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    JAL        -> List(PC_IMM   ,A_PC    ,B_IMM,   IMM_J  ,OP_ADD,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_PC4   ,WB_W),
    JALR       -> List(PC_JLR   ,A_RS1   ,B_IMM,   IMM_I  ,OP_ADD,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_PC4   ,WB_W),
    BEQ        -> List(PC_BRU   ,A_PC    ,B_IMM,   IMM_B  ,OP_XXX,  Br_EQ ,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_XXX   ,WB_N),
    BNE        -> List(PC_BRU   ,A_PC    ,B_IMM,   IMM_B  ,OP_XXX,  Br_NE ,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_XXX   ,WB_N),
    BLT        -> List(PC_BRU   ,A_PC    ,B_IMM,   IMM_B  ,OP_XXX,  Br_LT ,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_XXX   ,WB_N),
    BLTU       -> List(PC_BRU   ,A_PC    ,B_IMM,   IMM_B  ,OP_XXX,  Br_LTU,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_XXX   ,WB_N),
    BGE        -> List(PC_BRU   ,A_PC    ,B_IMM,   IMM_B  ,OP_XXX,  Br_GE ,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_XXX   ,WB_N),
    BGEU       -> List(PC_BRU   ,A_PC    ,B_IMM,   IMM_B  ,OP_XXX,  Br_GEU,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_XXX   ,WB_N),
    LB         -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_ADD,  Br_XXX,  WIDTH_B   ,SIG_S   ,MEM_N  ,DATA_MEM   ,WB_W),
    LH         -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_ADD,  Br_XXX,  WIDTH_H   ,SIG_S   ,MEM_N  ,DATA_MEM   ,WB_W),
    LW         -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_ADD,  Br_XXX,  WIDTH_W   ,SIG_S   ,MEM_N  ,DATA_MEM   ,WB_W),
    LWU        -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_ADD,  Br_XXX,  WIDTH_W   ,SIG_U   ,MEM_N  ,DATA_MEM   ,WB_W),
    LBU        -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_ADD,  Br_XXX,  WIDTH_B   ,SIG_U   ,MEM_N  ,DATA_MEM   ,WB_W),
    LHU        -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_ADD,  Br_XXX,  WIDTH_H   ,SIG_U   ,MEM_N  ,DATA_MEM   ,WB_W),
    SB         -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_S  ,OP_ADD,  Br_XXX,  WIDTH_B   ,SIG_X   ,MEM_W  ,DATA_XXX   ,WB_N),
    SH         -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_S  ,OP_ADD,  Br_XXX,  WIDTH_H   ,SIG_X   ,MEM_W  ,DATA_XXX   ,WB_N),
    SW         -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_S  ,OP_ADD,  Br_XXX,  WIDTH_W   ,SIG_X   ,MEM_W  ,DATA_XXX   ,WB_N),
    ADDI       -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_ADD,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SLTI       -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_SLT,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SLTIU      -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_SLTU, Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    ANDI       -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_AND,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    ORI        -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_OR ,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    XORI       -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_XOR,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SLLI       -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_SLL,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SRLI       -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_SRL,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SRAI       -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_SRA,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    ADD        -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_ADD,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SUB        -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_SUB,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SLL        -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_SLL,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SRL        -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_SRL,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SLTU       -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_SLTU, Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SLT        -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_SLT,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    SRA        -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_SRA,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    OR         -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_OR ,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    AND        -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_AND,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    XOR        -> List(PC_4     ,A_RS1   ,B_RS2,   IMM_X  ,OP_XOR,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_ALU   ,WB_W),
    //RV64I
    LD         -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_I  ,OP_ADD,  Br_XXX,  WIDTH_D   ,SIG_X   ,MEM_N  ,DATA_MEM   ,WB_W),
    SD         -> List(PC_4     ,A_RS1   ,B_IMM,   IMM_S  ,OP_ADD,  Br_XXX,  WIDTH_D   ,SIG_X   ,MEM_W  ,DATA_XXX   ,WB_N),
    END        -> List(PC_4     ,A_XXX   ,B_XXX,   IMM_X  ,OP_XXX,  Br_XXX,  WIDTH_X   ,SIG_X   ,MEM_N  ,DATA_XXX   ,WB_N),
    )
}
