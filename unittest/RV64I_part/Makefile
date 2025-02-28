# 变量设置
RISCV_PREFIX = riscv64-unknown-elf-
CC = $(RISCV_PREFIX)gcc
LD = $(RISCV_PREFIX)ld
OBJDUMP = $(RISCV_PREFIX)objdump
OBJCOPY = $(RISCV_PREFIX)objcopy

# 目标文件

test_src = $(wildcard *.S)
test_inst = $(test_src:.S=.inst)
test_res = $(test_src:.S=.res)

# 编译标志
CFLAGS = -march=rv64i -mabi=lp64 -static -nostdlib -nostartfiles -mcmodel=medany
LDFLAGS = -T link.ld

# 默认目标
all: $(test_res) $(test_inst) 

# 清理
clean:
	rm -f *.elf *.inst *.res 


%.elf: %.S
	$(CC) $(CFLAGS) -o $@ $< $(LDFLAGS)

%.inst: %.elf
	$(OBJDUMP) -d  $< |awk 'f {print $$2}; /_start/{f=1} ' > $@
%.res : %.elf
	spike pk $< | head -n 8 |awk '{for(i=2;i<=NF;i+=2) print $$i}' > $@
