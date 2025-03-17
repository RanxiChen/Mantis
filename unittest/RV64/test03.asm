    .data
    .align 2          # Ensure proper alignment for half-words (2-byte alignment)
array:  
    .byte 0x12, 0x34, 0x56, 0x78      # 4 bytes (positive values)
    .byte 0xFF, 0xFF, 0xFF, 0xFF      # 4 bytes (negative values - two's complement)
    .half 0x1234, 0x5678              # 2 half-words (positive values)
    .half 0xFFFF, 0x8000              # 2 half-words (negative values - two's complement)
    .word 0x12345678                  # 1 word (positive value)
    .word 0x80000000                  # 1 word (negative value - two's complement)
    .dword 0x1122334455667788         # 1 double-word (positive value)
    .dword 0x8000000000000000         # 1 double-word (negative value - two's complement)

    .text
    .global _start

_start:
    la a0, array        # Load address of array into a0

    # Load instructions (data access starts at correct, aligned positions)
    lb a1, 4(a0)        # Load byte from array[4] (negative byte - 0xFF)
    lbu a2, 5(a0)       # Load unsigned byte from array[5] (negative byte - 0xFF)
    lh a3, 8(a0)        # Load half-word from array[8] (negative half-word - 0xFFFF)
    lhu a4, 10(a0)      # Load unsigned half-word from array[10] (positive half-word - 0x8000)
    lw a5, 12(a0)       # Load word from array[12] (positive word - 0x12345678)
    lwu a6, 16(a0)      # Load unsigned word from array[16] (positive word - 0x12345678)
    ld a7, 20(a0)       # Load double-word from array[20] (positive double-word - 0x1122334455667788)

    # Store instructions (data access is done at aligned positions)
    sb a1, 30(a0)       # Store byte to array[30] (negative byte)
    sh a2, 28(a0)       # Store half-word to array[28] (negative byte)
    sw a3, 24(a0)       # Store word to array[24] (negative half-word)
    sd a4, 22(a0)       # Store double-word to array[22] (negative half-word)

    # Load and store operations using different data types
    ld t0, 20(a0)       # Load double-word from array[20] (positive value)
    sd t0, 0(a0)        # Store the loaded double-word to array[0]

loop:  
    j loop              # Infinite loop
