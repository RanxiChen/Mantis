# Mantis

## cpu
- [x] one single-cycle RV64I(part) core, Just compution (branch cmp_64i)
- [ ] single-cycle RV64I
- [ ] single-cycle RV64gc
- [ ] pipelined RV64gc

## tools required
1. [riscv-gnu-toolchain](https://github.com/riscv-collab/riscv-gnu-toolchain)

## Notes
1. At this time, using big 8-bits array as main memory, every address refer to 8-bits data.
And no register-related, mode-related,or interrupt-related instructions.
2. use Mem because I nead async-read and sync-write for single-cycle
