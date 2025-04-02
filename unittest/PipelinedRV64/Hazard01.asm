addi x1,x0,0
addi x2,x0,0
addi,x3,x0,0
addi,x4,x0,0
addi x1, x0, 10      # x1 = 10
addi x2, x1, 20      # x2 = 20
addi x3, x1, 30      # RAW hazard: x3 = x1 + x2 (needs x1 from previous instruction)
addi x4, x1, 40    
xor  x5, x4, x1      # RAW hazard: x5 = x4 XOR x3 (needs x4 from previous instruction)
or   x6, x5, x2      # RAW hazard: x6 = x5 OR x2 (needs x5 from previous instruction)
and  x7, x6, x5      # RAW hazard: x7 = x6 AND x5 (needs x6 from previous instruction)
slli x8, x7, 2       # RAW hazard: x8 = x7 << 2 (needs x7 from previous instruction)
srli x9, x8, 1       # RAW hazard: x9 = x8 >> 1 (needs x8 from previous instruction)
addi x10, x9, 5      # RAW hazard: x10 = x9 + 5 (needs x9 from previous instruction)
