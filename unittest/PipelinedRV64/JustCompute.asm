# Clear registers x2 and x3
addi x2, x0, 0       # Clear x2 by adding 0 to x0
addi x3, x0, 0       # Clear x3 by adding 0 to x0

# Perform computation with different registers for each instruction
addi x4, x0, 10      # Load immediate 10 into x4
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x5, x4,20
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x5, x0, 20      # Load immediate 20 into x5
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
add  x6, x4, x5      # Add x4 and x5, store in x6
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x7, x0, 5       # Load immediate 5 into x7
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
sub  x8, x6, x7      # Subtract x7 from x6, store in x8
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
slli x9, x8, 2       # Shift x8 left by 2 bits, store in x9
addi x10, x0, 15     # Load immediate 15 into x10
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
and  x11, x9, x10    # Bitwise AND of x9 and x10, store in x11
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
or   x12, x8, x10    # Bitwise OR of x8 and x10, store in x12
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
xor  x13, x11, x12   # Bitwise XOR of x11 and x12, store in x13
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
srli x14, x13, 1     # Shift x13 right by 1 bit, store in x14
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x15, x0, 100    # Load immediate 100 into x15
