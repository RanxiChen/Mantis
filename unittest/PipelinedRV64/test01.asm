# RISC-V CPU Test Suite
# This file includes tests for R-type, I-type, B-type, U-type, and J-type instructions
# Avoiding memory operations (load/store)

.text
.globl main

main:
    # Clear x2 and x3 which may have initial values in RARS
    addi x2, x0, 0
    addi x3, x0, 0

    # ==========================================
    # R-type instructions test
    # ==========================================
    
    # Test ADD
    addi x1, x0, 10      # x1 = 10
    addi x2, x0, 20      # x2 = 20
    add  x3, x1, x2      # x3 = x1 + x2 = 30
    
    # Test SUB
    sub  x4, x2, x1      # x4 = x2 - x1 = 10
    
    # Test AND
    addi x5, x0, 0x0F    # x5 = 0x0F (0b00001111)
    addi x6, x0, 0x33    # x6 = 0x33 (0b00110011)
    and  x7, x5, x6      # x7 = x5 & x6 = 0x03 (0b00000011)
    
    # Test OR
    or   x8, x5, x6      # x8 = x5 | x6 = 0x3F (0b00111111)
    
    # Test XOR
    xor  x9, x5, x6      # x9 = x5 ^ x6 = 0x3C (0b00111100)
    
    # Test SLL (Shift Left Logical)
    addi x10, x0, 1      # x10 = 1
    addi x11, x0, 3      # x11 = 3
    sll  x12, x10, x11   # x12 = x10 << x11 = 1 << 3 = 8
    
    # Test SRL (Shift Right Logical)
    addi x13, x0, 16     # x13 = 16
    srl  x14, x13, x11   # x14 = x13 >> x11 = 16 >> 3 = 2
    
    # Test SRA (Shift Right Arithmetic)
    addi x15, x0, -16    # x15 = -16
    sra  x16, x15, x11   # x16 = x15 >> x11 = -16 >> 3 = -2 (arithmetic)
    
    # Test SLT (Set Less Than)
    addi x17, x0, 5      # x17 = 5
    addi x18, x0, 10     # x18 = 10
    slt  x19, x17, x18   # x19 = (x17 < x18) ? 1 : 0 = 1
    slt  x20, x18, x17   # x20 = (x18 < x17) ? 1 : 0 = 0
    
    # Test SLTU (Set Less Than Unsigned)
    addi x21, x0, -1     # x21 = -1 (interpreted as a large unsigned number)
    sltu x22, x17, x21   # x22 = (x17 <u x21) ? 1 : 0 = 1
    
    # ==========================================
    # I-type instructions test
    # ==========================================
    
    # Test ADDI
    addi x23, x0, 42     # x23 = 42
    
    # Test XORI
    xori x24, x23, 0xFF  # x24 = x23 ^ 0xFF = 42 ^ 255 = 213
    
    # Test ORI
    ori  x25, x23, 0xFF  # x25 = x23 | 0xFF = 42 | 255 = 255
    
    # Test ANDI
    andi x26, x23, 0x0F  # x26 = x23 & 0x0F = 42 & 15 = 10
    
    # Test SLLI
    slli x27, x23, 2     # x27 = x23 << 2 = 42 << 2 = 168
    
    # Test SRLI
    srli x28, x23, 2     # x28 = x23 >> 2 = 42 >> 2 = 10
    
    # Test SRAI
    addi x29, x0, -42    # x29 = -42
    srai x30, x29, 2     # x30 = x29 >> 2 = -42 >> 2 = -11
    
    # Test SLTI
    slti x31, x23, 100   # x31 = (x23 < 100) ? 1 : 0 = 1
    
    # ==========================================
    # U-type instructions test
    # ==========================================
    
    # Test LUI (Load Upper Immediate)
    lui  x1, 0x12345     # x1 = 0x12345000
    
    # Test AUIPC (Add Upper Immediate to PC)
    auipc x2, 0x1        # x2 = PC + 0x1000
    
    # ==========================================
    # B-type instructions test
    # ==========================================
    
    # Initialize test values
    addi x3, x0, 10      # x3 = 10
    addi x4, x0, 20      # x4 = 20
    
    # Test BEQ (Branch if Equal)
    addi x5, x0, 1       # x5 = 1 (assume not taken)
    beq  x3, x4, beq_taken   # should not be taken
    addi x5, x0, 2       # x5 = 2 (if branch not taken)
beq_taken:
    
    # Test BNE (Branch if Not Equal)
    addi x6, x0, 1       # x6 = 1 (assume not taken)
    bne  x3, x3, bne_taken   # should not be taken
    addi x6, x0, 2       # x6 = 2 (if branch not taken)
bne_taken:
    
    # Test BLT (Branch if Less Than)
    addi x7, x0, 1       # x7 = 1 (assume not taken)
    blt  x4, x3, blt_taken   # should not be taken
    addi x7, x0, 2       # x7 = 2 (if branch not taken)
blt_taken:
    
    # Test BGE (Branch if Greater or Equal)
    addi x8, x0, 1       # x8 = 1 (assume not taken)
    bge  x3, x4, bge_taken   # should not be taken
    addi x8, x0, 2       # x8 = 2 (if branch not taken)
bge_taken:
    
    # Test BLTU (Branch if Less Than Unsigned)
    addi x9, x0, -1      # x9 = -1 (large unsigned)
    addi x10, x0, 1      # x10 = 1 (assume not taken)
    bltu x9, x3, bltu_taken  # should not be taken
    addi x10, x0, 2      # x10 = 2 (if branch not taken)
bltu_taken:
    
    # Test BGEU (Branch if Greater or Equal Unsigned)
    addi x11, x0, 1      # x11 = 1 (assume not taken)
    bgeu x3, x9, bgeu_taken  # should not be taken
    addi x11, x0, 2      # x11 = 2 (if branch not taken)
bgeu_taken:
    
    # ==========================================
    # Reverse case for branch instructions (taken)
    # ==========================================
    
    # Test BEQ (taken)
    addi x12, x0, 1      # x12 = 1
    beq  x3, x3, beq_reverse  # should be taken
    addi x12, x0, 99     # should be skipped
beq_reverse:
    
    # Test BNE (taken)
    addi x13, x0, 1      # x13 = 1
    bne  x3, x4, bne_reverse  # should be taken
    addi x13, x0, 99     # should be skipped
bne_reverse:
    
    # Test BLT (taken)
    addi x14, x0, 1      # x14 = 1
    blt  x3, x4, blt_reverse  # should be taken
    addi x14, x0, 99     # should be skipped
blt_reverse:
    
    # Test BGE (taken)
    addi x15, x0, 1      # x15 = 1
    bge  x4, x3, bge_reverse  # should be taken
    addi x15, x0, 99     # should be skipped
bge_reverse:
    
    # Test BLTU (taken)
    addi x16, x0, 1      # x16 = 1
    bltu x3, x9, bltu_reverse  # should be taken
    addi x16, x0, 99     # should be skipped
bltu_reverse:
    
    # Test BGEU (taken)
    addi x17, x0, 1      # x17 = 1
    bgeu x9, x3, bgeu_reverse  # should be taken
    addi x17, x0, 99     # should be skipped
bgeu_reverse:
    
    # ==========================================
    # J-type instructions test
    # ==========================================
    
    # Test JAL (Jump and Link)
    addi x18, x0, 1      # x18 = 1
    jal  x19, jal_target # x19 = PC+4, PC = jal_target
    addi x18, x0, 99     # should be skipped
jal_target:
    addi x20, x0, 2      # x20 = 2
    
    # Test JALR (Jump and Link Register)
    addi x21, x0, 1      # x21 = 1
    la   x22, jalr_target
    jalr x23, 0(x22)     # x23 = PC+4, PC = jalr_target
    addi x21, x0, 99     # should be skipped
jalr_target:
    addi x24, x0, 2      # x24 = 2
    
    # End of test
end_test:
    # Use a sequence of operations to create a recognizable end pattern
    addi x25, x0, 1       # Start with 1
    slli x25, x25, 8      # Shift left by 8 (x25 = 256)
    addi x25, x25, 0xAB   # Add 171 (x25 = 427)
    # Final value in x25 = 427 (0x1AB)

    # Loop forever for simulation purposes
infinite_loop:
    j infinite_loop

# Expected results:
# x1  = 0x12345000
# x2  = PC + 0x1000 (value depends on program location)
# x3  = 10
# x4  = 20
# x5  = 2 (branch not taken)
# x6  = 2 (branch not taken)
# x7  = 2 (branch not taken)
# x8  = 2 (branch not taken)
# x9  = -1 (0xFFFFFFFF)
# x10 = 2 (branch not taken)
# x11 = 2 (branch not taken)
# x12 = 1 (branch taken)
# x13 = 1 (branch taken)
# x14 = 1 (branch taken)
# x15 = 1 (branch taken)
# x16 = 1 (branch taken)
# x17 = 1 (branch taken)
# x18 = 1 (branch taken in JAL)
# x19 = PC+4 of the JAL instruction
# x20 = 2
# x21 = 1 (branch taken in JALR)
# x22 = address of jalr_target
# x23 = PC+4 of the JALR instruction
# x24 = 2
# x25 = 427 (0x1AB) (end marker)
# Other registers should be as described in the comments