/* Linker script for RISC-V CPU test */
/* Places code at address 0 */

OUTPUT_ARCH("riscv")
ENTRY(_start)

SECTIONS
{
  /* Code section starts at address 0 */
  . = 0x00000000;
  
  .text : {
    *(.text.init)
    *(.text)
  }
  
  /* Read-only data */
  .rodata : {
    *(.rodata)
    *(.rodata.*)
  }
  
  /* Read-write data (initialized) */
  .data : {
    *(.data)
    *(.data.*)
  }
  
  /* Read-write data (uninitialized) */
  .bss : {
    *(.bss)
    *(.bss.*)
  }
  
  /* Stack and heap can be added if needed in the future */
  
  /* Discard unnecessary sections */
  /DISCARD/ : {
    *(.note.*)
    *(.comment)
  }
}
