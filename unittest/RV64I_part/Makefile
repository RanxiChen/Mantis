export RISCV = /home/chen/RVCORE/Mantis/tool/riscv-gcc/riscv

cmp1:cmp1.S
	$(RISCV)/bin/riscv64-unknown-elf-gcc -static -o cmp1 cmp1.S -mabi=lp64 -march=rv64i -nostartfiles -nostdlib 
