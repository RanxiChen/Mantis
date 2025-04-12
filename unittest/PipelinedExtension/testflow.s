.text
.global _start
_start:
	addi x1,x0,0x10
	mul x2,x1,x1
	mul x3,x2,x2
	mul x4,x3,x3
	mul x5,x4,x4
	mul x6,x5,x5
	ecall
