# 测试案例1：基本算术和寄存器操作

    addi x1, x0, 10      # x1 = 0 + 10 = 10
    addi x2, x0, 20      # x2 = 0 + 20 = 20
    add  x3, x1, x2      # x3 = x1 + x2 = 30
    sub  x4, x3, x1      # x4 = x3 - x1 = 20

# 机器码(十六进制):
# addi x1, x0, 10:   0x00a00093
# addi x2, x0, 20:   0x01400113
# add  x3, x1, x2:   0x002081b3
# sub  x4, x3, x1:   0x40118233


# 测试案例2：位移操作

    addi x1, x0, -8      # x1 = -8
    slli x2, x1, 2       # x2 = x1 << 2 (逻辑左移)
    srli x3, x1, 2       # x3 = x1 >> 2 (逻辑右移)
    srai x4, x1, 2       # x4 = x1 >> 2 (算术右移)

# 机器码(十六进制):
# addi x1, x0, -8:   0xff800093
# slli x2, x1, 2:    0x00209113
# srli x3, x1, 2:    0x0020d193
# srai x4, x1, 2:    0x4020d213


# 测试案例3：直接使用立即数的分支和跳转

    addi x1, x0, 10      # x1 = 10
    addi x2, x0, 20      # x2 = 20
    beq  x1, x2, 8       # 如果x1 == x2，跳转8字节
    addi x1, x1, 1       # x1 += 1
    jal  x0, -8          # 无条件跳转回前8字节
    addi x3, x0, 1       # x3 = 1

# 机器码(十六进制):
# addi x1, x0, 10:   0x00a00093
# addi x2, x0, 20:   0x01400113
# beq  x1, x2, 8:    0x00208463
# addi x1, x1, 1:    0x00108093
# jal  x0, -8:       0xff9ff06f
# addi x3, x0, 1:    0x00100193


# 测试案例4：加载和存储指令

    addi x1, x0, 100     # x1 = 100 (地址)
    addi x2, x0, 42      # x2 = 42 (数据)
    sd   x2, 0(x1)       # 存储x2到地址x1
    ld   x3, 0(x1)       # 从地址x1加载到x3
    lb   x4, 0(x1)       # 加载字节
    lh   x5, 0(x1)       # 加载半字
    lw   x6, 0(x1)       # 加载字

# 机器码(十六进制):
# addi x1, x0, 100:  0x06400093
# addi x2, x0, 42:   0x02a00113
# sd   x2, 0(x1):    0x00213023
# ld   x3, 0(x1):    0x0000b183
# lb   x4, 0(x1):    0x00008203
# lh   x5, 0(x1):    0x00009283
# lw   x6, 0(x1):    0x0000a303


# 测试案例5：64位操作

    lui   x1, 0xABCDE    # 加载上立即数到x1
    addiw x2, x0, -1     # 32位加法，结果符号扩展到64位
    slliw x3, x2, 4      # 32位逻辑左移，结果符号扩展到64位
    srliw x4, x2, 4      # 32位逻辑右移，结果符号扩展到64位
    sraiw x5, x2, 4      # 32位算术右移，结果符号扩展到64位
    addw  x6, x2, x3     # 32位加法，结果符号扩展到64位
    subw  x7, x2, x3     # 32位减法，结果符号扩展到64位

# 机器码(十六进制):
# lui   x1, 0xABCDE:  0xabcde0b7
# addiw x2, x0, -1:   0xfff0011b
# slliw x3, x2, 4:    0x0041119b
# srliw x4, x2, 4:    0x0041d21b
# sraiw x5, x2, 4:    0x4041d29b
# addw  x6, x2, x3:   0x00310333
# subw  x7, x2, x3:   0x403103b3


# 测试案例6：直接立即数的复杂控制流

    addi x1, x0, 0       # x1 = 0 (计数器)
    addi x2, x0, 10      # x2 = 10 (限制)
    bge  x1, x2, 28      # 如果x1 >= x2，跳转28字节
    addi x3, x0, 0       # x3 = 0 (内部计数器)
    addi x4, x0, 5       # x4 = 5 (内部限制)
    bge  x3, x4, 12      # 如果x3 >= x4，跳转12字节
    addi x3, x3, 1       # x3 += 1
    jal  x0, -8          # 跳回前8字节
    addi x1, x1, 1       # x1 += 1
    jal  x0, -24         # 跳回前24字节
    addi x5, x0, 1       # x5 = 1 (结束标记)

# 机器码(十六进制):
# addi x1, x0, 0:      0x00000093
# addi x2, x0, 10:     0x00a00113
# bge  x1, x2, 28:     0x0020d863
# addi x3, x0, 0:      0x00000193
# addi x4, x0, 5:      0x00500213
# bge  x3, x4, 12:     0x0041d663
# addi x3, x3, 1:      0x00118193
# jal  x0, -8:         0xff9ff06f
# addi x1, x1, 1:      0x00108093
# jal  x0, -24:        0xfe9ff06f
# addi x5, x0, 1:      0x00100293


# 测试案例7：U类和J类指令

    lui    x1, 0x12345   # 加载上立即数
    auipc  x2, 0x1000    # 加载PC相对上立即数
    jal    x3, 8         # 跳转并链接到后8字节
    addi   x4, x0, 10    # 不应执行
    addi   x5, x0, 20    # x5 = 20

# 机器码(十六进制):
# lui    x1, 0x12345:  0x123450b7
# auipc  x2, 0x1000:   0x01000117
# jal    x3, 8:        0x00800167
# addi   x4, x0, 10:   0x00a00213
# addi   x5, x0, 20:   0x01400293


# 测试案例8：更多的立即数指令

    ori    x1, x0, 0x123     # 逻辑或立即数
    andi   x2, x1, 0xFF      # 逻辑与立即数
    xori   x3, x1, 0x55      # 逻辑异或立即数
    slti   x4, x1, 0x200     # 有符号比较立即数
    sltiu  x5, x1, 0x200     # 无符号比较立即数

# 机器码(十六进制):
# ori    x1, x0, 0x123:  0x12306093
# andi   x2, x1, 0xFF:   0x0ff0f113
# xori   x3, x1, 0x55:   0x0550c193
# slti   x4, x1, 0x200:  0x2000a213
# sltiu  x5, x1, 0x200:  0x2000b293


# 测试案例9：各种存储指令

    addi   x1, x0, 100     # 基址
    addi   x2, x0, -1      # 存储数据
    sb     x2, 4(x1)       # 存储字节
    sh     x2, 8(x1)       # 存储半字
    sw     x2, 12(x1)      # 存储字
    sd     x2, 16(x1)      # 存储双字

# 机器码(十六进制):
# addi   x1, x0, 100:   0x06400093
# addi   x2, x0, -1:    0xfff00113
# sb     x2, 4(x1):     0x00208223
# sh     x2, 8(x1):     0x00209423
# sw     x2, 12(x1):    0x0020a623
# sd     x2, 16(x1):    0x0020b823


# 测试案例10：JALR指令和寄存器间接跳转

    addi   x1, x0, 16      # x1 = 16
    jalr   x2, x1, 0       # 跳转到x1地址并链接
    addi   x3, x0, 5       # 不会执行
    addi   x4, x0, 6       # 会执行
    jalr   x0, x2, 0       # 返回到链接地址

# 机器码(十六进制):
# addi   x1, x0, 16:    0x01000093
# jalr   x2, x1, 0:     0x00008117
# addi   x3, x0, 5:     0x00500193
# addi   x4, x0, 6:     0x00600213
# jalr   x0, x2, 0:     0x00010067


# 测试案例11：向前跳转（负偏移量）

    addi   x1, x0, 1       # x1 = 1
    addi   x2, x0, 2       # x2 = 2
    addi   x3, x0, 3       # x3 = 3
    jal    x0, 12          # 向前跳转12字节（跳过下3条指令）
    addi   x4, x0, 4       # 不会执行
    addi   x5, x0, 5       # 不会执行
    addi   x6, x0, 6       # 不会执行
    addi   x7, x0, 7       # x7 = 7
    jal    x0, -28         # 向后跳转到开头（-28字节）
    addi   x8, x0, 8       # 不应执行

# 机器码(十六进制):
# addi   x1, x0, 1:     0x00100093
# addi   x2, x0, 2:     0x00200113
# addi   x3, x0, 3:     0x00300193
# jal    x0, 12:        0x00c0006f
# addi   x4, x0, 4:     0x00400213
# addi   x5, x0, 5:     0x00500293
# addi   x6, x0, 6:     0x00600313
# addi   x7, x0, 7:     0x00700393
# jal    x0, -28:       0xfe5ff06f
# addi   x8, x0, 8:     0x00800413


# 测试案例12：各种负立即数指令

    addi   x1, x0, -50      # 负立即数加法
    ori    x2, x0, -100     # 负立即数逻辑或
    andi   x3, x0, -1       # 负立即数逻辑与
    xori   x4, x0, -20      # 负立即数逻辑异或
    slti   x5, x0, -10      # 负立即数有符号比较
    sltiu  x6, x0, -5       # 负立即数无符号比较（注意：被视为大正数）

# 机器码(十六进制):
# addi   x1, x0, -50:   0xfce00093
# ori    x2, x0, -100:  0xf9c06113
# andi   x3, x0, -1:    0xfff0f193
# xori   x4, x0, -20:   0xfec0c213
# slti   x5, x0, -10:   0xff60a293
# sltiu  x6, x0, -5:    0xffb0b313


# 测试案例13：各种条件分支向前跳转

    addi  x1, x0, 10       # x1 = 10
    addi  x2, x0, 20       # x2 = 20
    addi  x3, x0, 0        # x3 = 0
    beq   x1, x2, 20       # 不相等，不跳转
    bne   x1, x2, 16       # 不相等，跳转16字节
    addi  x3, x3, 1        # 不执行
    addi  x3, x3, 2        # 不执行
    addi  x3, x3, 3        # 不执行
    addi  x3, x3, 4        # 不执行
    blt   x2, x1, 20       # x2 > x1，不跳转
    bge   x2, x1, 16       # x2 > x1，跳转16字节
    addi  x3, x3, 5        # 不执行
    addi  x3, x3, 6        # 不执行
    addi  x3, x3, 7        # 不执行
    addi  x3, x3, 8        # 不执行
    bltu  x2, x1, 20       # x2 > x1，不跳转
    bgeu  x2, x1, 16       # x2 > x1，跳转16字节
    addi  x3, x3, 9        # 不执行
    addi  x3, x3, 10       # 不执行
    addi  x3, x3, 11       # 不执行
    addi  x3, x3, 12       # 不执行
    addi  x4, x0, 100      # x4 = 100
    
# 机器码(十六进制):
# addi  x1, x0, 10:      0x00a00093
# addi  x2, x0, 20:      0x01400113
# addi  x3, x0, 0:       0x00000193
# beq   x1, x2, 20:      0x00208a63
# bne   x1, x2, 16:      0x00209863
# addi  x3, x3, 1:       0x00118193
# addi  x3, x3, 2:       0x00218193
# addi  x3, x3, 3:       0x00318193
# addi  x3, x3, 4:       0x00418193
# blt   x2, x1, 20:      0x0010aa63
# bge   x2, x1, 16:      0x0011d863
# addi  x3, x3, 5:       0x00518193
# addi  x3, x3, 6:       0x00618193
# addi  x3, x3, 7:       0x00718193
# addi  x3, x3, 8:       0x00818193
# bltu  x2, x1, 20:      0x0010ba63
# bgeu  x2, x1, 16:      0x0011e863
# addi  x3, x3, 9:       0x00918193
# addi  x3, x3, 10:      0x00a18193
# addi  x3, x3, 11:      0x00b18193
# addi  x3, x3, 12:      0x00c18193
# addi  x4, x0, 100:     0x06400213


# 测试案例14：带负偏移量的存储和加载指令

    addi x1, x0, 100      # x1 = 100 (基址)
    addi x2, x0, -1       # x2 = -1 (数据)
    sd   x2, -8(x1)       # 存储到[x1-8]
    ld   x3, -8(x1)       # 从[x1-8]加载
    sw   x2, -4(x1)       # 存储到[x1-4]
    lw   x4, -4(x1)       # 从[x1-4]加载
    sh   x2, -2(x1)       # 存储到[x1-2]
    lh   x5, -2(x1)       # 从[x1-2]加载
    sb   x2, -1(x1)       # 存储到[x1-1]
    lb   x6, -1(x1)       # 从[x1-1]加载

# 机器码(十六进制):
# addi x1, x0, 100:     0x06400093
# addi x2, x0, -1:      0xfff00113
# sd   x2, -8(x1):      0xfe213c23
# ld   x3, -8(x1):      0xff80b183
# sw   x2, -4(x1):      0xfe212e23
# lw   x4, -4(x1):      0xffc0a203
# sh   x2, -2(x1):      0xfe211f23
# lh   x5, -2(x1):      0xffe09283
# sb   x2, -1(x1):      0xfe210fa3
# lb   x6, -1(x1):      0xfff08303


# 测试案例15：32位带符号扩展的加载指令

    addi x1, x0, 100      # x1 = 100 (基址)
    addi x2, x0, -1       # x2 = -1 (数据为0xFFFFFFFFFFFFFFFF)
    sw   x2, 0(x1)        # 存储32位到[x1]
    lwu  x3, 0(x1)        # 无符号加载 (应为0x00000000FFFFFFFF)
    lw   x4, 0(x1)        # 有符号加载 (应为0xFFFFFFFFFFFFFFFF)
    sh   x2, 4(x1)        # 存储16位到[x1+4]
    lhu  x5, 4(x1)        # 无符号加载 (应为0x000000000000FFFF)
    lh   x6, 4(x1)        # 有符号加载 (应为0xFFFFFFFFFFFFFFFF)
    sb   x2, 8(x1)        # 存储8位到[x1+8]
    lbu  x7, 8(x1)        # 无符号加载 (应为0x00000000000000FF)
    lb   x8, 8(x1)        # 有符号加载 (应为0xFFFFFFFFFFFFFFFF)

# 机器码(十六进制):
# addi x1, x0, 100:     0x06400093
# addi x2, x0, -1:      0xfff00113
# sw   x2, 0(x1):       0x0020a023
# lwu  x3, 0(x1):       0x0000e183
# lw   x4, 0(x1):       0x0000a203
# sh   x2, 4(x1):       0x00211223
# lhu  x5, 4(x1):       0x0040d283
# lh   x6, 4(x1):       0x00409303
# sb   x2, 8(x1):       0x00210423
# lbu  x7, 8(x1):       0x0080c383
# lb   x8, 8(x1):       0x00808403


# 测试案例16：LUI和AUIPC与负立即数

    lui   x1, 0xFFFFF     # 加载上立即数 (会被解释为负值)
    addi  x2, x1, 0       # 复制x1到x2
    lui   x3, 0x80000     # 最小负数
    auipc x4, 0xFFFFF     # PC相对上立即数 (会被解释为负值)
    auipc x5, 0x80000     # 最小负数的PC相对

# 机器码(十六进制):
# lui   x1, 0xFFFFF:     0xfffff0b7
# addi  x2, x1, 0:       0x00008113
# lui   x3, 0x80000:     0x800001b7
# auipc x4, 0xFFFFF:     0xfffff217
# auipc x5, 0x80000:     0x80000297


# 测试案例17：用立即数测试RV64I特有的w指令

    addi  x1, x0, -100     # x1 = -100 (64位)
    addiw x2, x0, -100     # x2 = -100 (32位符号扩展到64位)
    slliw x3, x2, 4        # 32位左移4位后符号扩展
    srliw x4, x2, 4        # 32位逻辑右移4位后符号扩展
    sraiw x5, x2, 4        # 32位算术右移4位后符号扩展
    
    #addi  x6, x0, 0x7FFFFFFF    # x6 = 0x7FFFFFFF (最大32位正数)
    #addi  x7, x6, 1             # x7 = 0x80000000 (溢出到负数，在64位中仍为正数)
    #addiw x8, x6, 1             # x8 = 0xFFFFFFFF80000000 (32位溢出并符号扩展)

# 机器码(十六进制):
# addi  x1, x0, -100:       0xf9c00093
# addiw x2, x0, -100:       0xf9c0011b
# slliw x3, x2, 4:          0x0041119b
# srliw x4, x2, 4:          0x0041d21b
# sraiw x5, x2, 4:          0x4041d29b
# addi  x6, x0, 0x7FFFFFFF: 0x80000537 (这里需要使用多条指令)
                           # 0xfff50513
# addi  x7, x6, 1:          0x00150593
# addiw x8, x6, 1:          0x00150c1b


# 测试案例18：极端立即数边界案例

    addi  x1, x0, 2047      # 最大正12位立即数
    addi  x2, x0, -2048     # 最小负12位立即数
    
    # 测试分支指令的极端偏移
    addi  x3, x0, 0
    addi  x4, x0, 5
    beq   x3, x3, 4094      # 最大正向分支偏移
    addi  x5, x0, 1         # 不执行
    
    addi  x6, x0, 0
    beq   x6, x6, -4096     # 最大负向分支偏移
    addi  x7, x0, 1         # 不执行
    
    # 测试JAL的极端偏移
    #jal   x8, 1048574       # 最大正向JAL偏移
    #jal   x9, -1048576      # 最大负向JAL偏移

# 机器码(十六进制):
# addi  x1, x0, 2047:    0x7ff00093
# addi  x2, x0, -2048:   0x80000113
# addi  x3, x0, 0:       0x00000193
# addi  x4, x0, 5:       0x00500213
# beq   x3, x3, 4094:    0x7e018fe3
# addi  x5, x0, 1:       0x00100293
# addi  x6, x0, 0:       0x00000313
# beq   x6, x6, -4096:   0x80030fe3
# addi  x7, x0, 1:       0x00100393
# jal   x8, 1048574:     0x7ffff3ef
# jal   x9, -1048576:    0x800004ef
