package core.pipelined

import chisel3._
import chisel3.util._

class DecodeModule extends Module {
    val io = IO(new Bundle{
        val in = Input(new PassPCInstBundle)
        val out = Output(new PassuInstBundle)
        val fetchrf = Flipped(new RegFileReadPort)
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
    /*
    //printf("[ID] src1: 0x%x, src2: 0x%x\t",io.out.src1,io.out.src2)
    printf("[ID]")
    when(CtrlSigs(1) === A_PC){
        printf("src1:0x%x from PC    ",io.out.src1)

    }.otherwise{
        printf("src1:0x%x from Reg%d ",io.out.src1,inst_rs1)
    }
    when(CtrlSigs(2) === B_PC){
        printf("src2:0x%x from PC   ",io.out.src2)

    }.elsewhen(CtrlSigs(2) === B_IMM){
        printf("src2:0x%x from Imm   ",io.out.src2)

    }.otherwise{
        printf("src2:0x%x from Reg%d ",io.out.src2,inst_rs2)
    }*/
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
class DecodeModuleProbeIO extends Bundle{
    val src1 = Output(UInt(64.W))
    val src2 = Output(UInt(64.W))
    val src1_source = Output(UInt(1.W))
    val src2_source = Output(UInt(2.W))
    val imm_u = Output(UInt(64.W))
    val inst_rs1 = Output(UInt(5.W))
    val inst_rs2 = Output(UInt(5.W))
}
trait DecodeModuleProbe {
    val probe = IO(new DecodeModuleProbeIO)
    println("ID Module with probe")
}

class DecodeModuleWithProbe extends DecodeModule with DecodeModuleProbe {
    probe.src1 := io.out.src1
    probe.src2 := io.out.src2
    probe.src1_source := io.out.alu_op
    probe.src2_source := io.out.bru_op
    probe.imm_u := io.out.imm_u
    probe.inst_rs1 := inst_rs1
    probe.inst_rs2 := inst_rs2
}

object DecodeModule {
    def apply(probe: Boolean=false): DecodeModule = {
        if (probe) {
            val decodeModule = Module(new DecodeModuleWithProbe)
            decodeModule
        } else {
            val decodeModule = Module(new DecodeModule)
            decodeModule
        }
    }
}