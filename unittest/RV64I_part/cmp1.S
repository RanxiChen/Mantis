.global _start
_start:
    # R型指令测试
    add a0, zero, zero   # a0 = 0
    addi a0, a0, 10      # a0 = 10
    addi a1, zero, 20    # a1 = 20
    add a2, a0, a1       # a2 = 30
    sub a3, a2, a0       # a3 = 20
    
    # 更多I型指令测试
    slli a4, a0, 2       # a4 = 40
    srli a5, a4, 1       # a5 = 20
    
    # 逻辑运算
    and a6, a0, a1
    or a7, a0, a1
    
