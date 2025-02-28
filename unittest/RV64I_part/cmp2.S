#.section .text
.globl _start

_start:
    # 将寄存器x1设置为5
    addi a1, x0, 5
    
    # 将寄存器x2设置为10
    addi a2, x0, 10
    
    # 将x1和x2相加，结果存储在x3中
    add a3, a1, a2
    
    # 对x3执行按位与操作，与值0xF
    andi a4, a3, 0xF
    
