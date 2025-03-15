    .text               # Code section
    .globl _start       # Global entry point

_start:
    lui x1, 0x12345      # Load upper immediate (for large numbers)
    auipc x2, 0x1        # Load PC-relative address (for position independence)

    li x3, 100           # Counter for loop control
    li x4, 20            # Another counter
    la x5, array         # Load base address of array

loop_main:
    add x6, x3, x4       # x6 = x3 + x4
    sub x7, x3, x4       # x7 = x3 - x4
    xor x8, x6, x7       # x8 = x6 ^ x7
    or x9, x8, x4        # x9 = x8 | x4
    and x10, x9, x3      # x10 = x9 & x3
    sll x11, x10, x4     # Shift left
    srl x12, x11, x4     # Shift right
    sra x13, x12, x4     # Arithmetic shift

    lw x14, 0(x5)        # Load first word from array
    sw x14, 4(x5)        # Store it at next location

    # Branches to different paths to exercise branching behavior
    beq x3, x4, branch_1
    bne x6, x7, branch_2
    blt x8, x9, branch_3
    bge x10, x11, branch_4
    bltu x12, x13, branch_5
    bgeu x14, x3, branch_6

branch_1:
    addi x3, x3, -3
    j continue_loop

branch_2:
    addi x4, x4, -5
    j continue_loop

branch_3:
    addi x5, x5, 8
    j continue_loop

branch_4:
    addi x6, x6, 4
    j continue_loop

branch_5:
    addi x7, x7, 2
    j continue_loop

branch_6:
    addi x8, x8, 1
    j continue_loop

continue_loop:
    addi x3, x3, -1    # Decrease main counter
    bne x3, zero, loop_main  # Loop until x3 reaches zero

end:
    j _start           # Restart the whole process after completion

    .data              # Data section
array:
    .word 10, 20, 30, 40, 50, 60, 70, 80  # Example data in memory
