OUTPUT_ARCH("riscv")
ENTRY(_start)

SECTIONS
{
  /* 程序代码将从0x80000000开始，这是RISC-V的标准起始地址 */
  . = 0x80000000;
  
  .text : {
    *(.text)
  }
  
}
