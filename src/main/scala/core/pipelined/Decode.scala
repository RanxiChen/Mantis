package core.pipelined

import chisel3._
import chisel3.util._

class DecodeModule extends Module {
    val io = IO(new Bundle{
        val in = Input(new PassPCInstBundle)
        val out = Output(new PassuInstBundle)
        val fetchrf = Input(new RegFileReadPort)
    })
    import core.Signal._
    import core.IDMap._
    val CtrlSigs = ListLookup(io.in.inst, defaultCtrl, instrMap)
    val inst_rd = io.in.inst(11,7)
    val inst_rs1 = io.in.inst(19,15)
    val inst_rs2 = io.in.inst(24,20)
    //fetch from regfile
    io.fetchrf.src1_addr := inst_rs1
    io.fetchrf.src2_addr := inst_rs2

    val immgen = Module(new core.ImmGen_RV64I)
    immgen.io.inst := io.in.inst
    val immValue = Wire(UInt(64.W))
    immValue := immgen.io.imm
    immgen.io.sel := CtrlSigs(3)

    io.out.src1 := Mux(CtrlSigs(1) === A_PC,io.in.pc,io.fetchrf.src1_data )
    io.out.src2 := MuxCase(0.U,
    IndexedSeq(
        (CtrlSigs(2) === B_PC) -> io.in.pc,
        (CtrlSigs(2) === B_IMM) -> immValue,
        (CtrlSigs(2) === B_RS2) -> io.fetchrf.src2_data
    )
    )
    printf("[ID] src1: 0x%x, src2: 0x%x\n",io.out.src1,io.out.src2)
    io.out.alu_op := CtrlSigs(4)
    io.out.bru_op := CtrlSigs(5)
    io.out.mem_width := CtrlSigs(6)
    io.out.mem_sig := CtrlSigs(7)
    io.out.mem_we := CtrlSigs(8)
    io.out.wb_sel := CtrlSigs(9)
    io.out.wb_en := CtrlSigs(10)
    //io.out.pc_sel := CtrlSigs(0)
    io.out.pc_4 := io.in.pc_4
    io.out.rd_addr := inst_rd
    io.out.rs2_data := io.fetchrf.src2_data
    io.out.imm_u := immValue
}

