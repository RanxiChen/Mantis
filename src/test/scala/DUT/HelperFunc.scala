package core.pipelined
import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import core.Signal._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import core.Colors._
//import chiseltest._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._

object dumpPU {
    def bubbleOrPC(notbubble:Bool,pc:BigInt,numeric_width:Int =3):String = {
        if(notbubble.peek().litToBoolean){
            s"PC:0x%0${numeric_width}x".format(pc)
        }else{
            s"%${numeric_width+5}s".format("bubble")
        }
    }
    def resetDut(dut:core.pipelined.TinySOC):Unit = {
        dut.reset.poke(true.B)
        dut.clock.step()
        dut.reset.poke(false.B)
        println("Run after reset")
    }
    def dumpPC(dut:core.pipelined.TinySOC,numeric_width:Int = 3):String = {
        "[" + s"%0${numeric_width}x".format(dut.io.mcycle.peek().litValue) + "]"
    }
    def dumpIF(dut:core.pipelined.TinySOC,numeric_width:Int =3):String = {
        val colorChar = getcolor(dut.io.IF.pc.peek().litValue.toInt/4)
        colorChar + (s"[IF] from 0x%0${numeric_width}x get Inst: %08x").format(dut.io.IF.pc.peek().litValue,dut.io.IF.inst.peek().litValue) + RESET        
    }
    def dumpID(dut:core.pipelined.TinySOC,numeric_width:Int =3):String = {
        val src1 = dut.io.ID.src1.peek().litValue
        val src2 = dut.io.ID.src2.peek().litValue
        var src1_source = {
            if(dut.io.ID.src1_source.peek().litValue == A_PC.litValue){
                "PC " + "  "
            }else{
                "Reg" + f"${dut.io.ID.inst_rs1.peek().litValue}%02d"
            }
        }
        var src2_source = {
            if(dut.io.ID.src2_source.peek().litValue == B_RS2.litValue){
                ("Reg"+ f"${dut.io.ID.inst_rs2.peek().litValue}%02d")
            }else if(dut.io.ID.src2_source.peek().litValue == B_IMM.litValue){
                ("Imm" + "  ")
            }else{
                ("PC " + "  ")
            }
        }
        val colorChar = getcolor(dut.io.ID.pc.peek().litValue.toInt/4)
        colorChar + s"[ID] src1:0x%0${numeric_width}x from ${src1_source} src2:0x%0${numeric_width}x from ${src2_source} ".format(src1,src2) +
            dumpPU.bubbleOrPC(dut.io.ID.notbubble,dut.io.ID.pc.peek().litValue,numeric_width) + RESET
    }
    def ALUop(op:UInt):String = {
        op.peek().litValue.toInt match {
                        case 0 => " add"
                        case 1 => " sub"
                        case 2 => " and"
                        case 3 => "  or"
                        case 4 => " xor"
                        case 5 => " sll"
                        case 6 => " srl"
                        case 7 => " sra"
                        case 8 => " slt"
                        case 9 => "sltu"
                        case _ => "XXXX"
                    }
        
    }
    def dumpEXE(dut:core.pipelined.TinySOC,numeric_width:Int =3):String = {
        val colorChar = getcolor(dut.io.EXE.pc.peek().litValue.toInt/4)
        colorChar + s"[EXE]%s %0${numeric_width}x %0${numeric_width}x -> 0x%0${numeric_width}x ".
        format(dumpPU.ALUop(dut.io.EXE.alu_op),dut.io.EXE.src1.peek().litValue,
            dut.io.EXE.src2.peek().litValue,
            dut.io.EXE.alu_res.peek().litValue) + dumpPU.bubbleOrPC(dut.io.EXE.notbubble,dut.io.EXE.pc.peek().litValue,numeric_width) + RESET
    }
    def mem_sig(sig:Bool):String = {
        sig.peek().litToBoolean match {
            case true => "S"
            case false => "U"
        }
    }
    def mem_width(width:UInt):String = {
        width.peek().litValue.toInt match {
            case 0 => "B"
            case 1 => "H"
            case 2 => "W"
            case 4 => "D"
            case _ => "X"
        }
    }
    def dumpMEM(dut:core.pipelined.TinySOC,numeric_width:Int =3):String = {
        val colorChar = getcolor(dut.io.MEM.pc.peek().litValue.toInt/4)
        if(dut.io.MEM.WE.peek().litToBoolean){
            colorChar + 
                s"[MEM] Write:0x%0${numeric_width}x%s%s  to  0x%0${numeric_width}x ".
                format(dut.io.MEM.wdata.peek().litValue,
                    dumpPU.mem_sig(dut.io.MEM.sig),
                    dumpPU.mem_width(dut.io.MEM.bfwd),
                    dut.io.MEM.addr.peek().litValue)+ dumpPU.bubbleOrPC(dut.io.MEM.notbubble,dut.io.MEM.pc.peek().litValue,numeric_width) + RESET
        }else{
            colorChar + 
                s"[MEM]  Read:0x%0${numeric_width}x%s%s from 0x%0${numeric_width}x ".
                format(dut.io.MEM.rdata.peek().litValue,
                    dumpPU.mem_sig(dut.io.MEM.sig),
                    dumpPU.mem_width(dut.io.MEM.bfwd),
                    dut.io.MEM.addr.peek().litValue) + dumpPU.bubbleOrPC(dut.io.MEM.notbubble,dut.io.MEM.pc.peek().litValue,numeric_width) +RESET
        }
    }
    def wb_sel(sel:UInt):String = {
        sel.peek().litValue.toInt match {
            case 0 => "alu"
            case 1 => "imm"
            case 2 => "mem"
            case 3 => "pc4"
            case _ => "XXX"
        }
    }
    def WriteRead(sel:Bool):String = {
        sel.peek().litToBoolean match {
            case true => "Write"
            case false => "Read"
        }
    }
    def dumpWB(dut:core.pipelined.TinySOC,numeric_width:Int =3):String = {
        val colorChar = getcolor(dut.io.WB.pc.peek().litValue.toInt/4) 
        if(dut.io.WB.WriteEnable.peek().litToBoolean){
            colorChar + s"[WB]Write 0x%0${numeric_width}x to Reg%02d from %s ".
            format(dut.io.WB.rd_data.peek().litValue,
                dut.io.WB.rd_addr.peek().litValue,
                dumpPU.wb_sel(dut.io.WB.wb_sel)) + dumpPU.bubbleOrPC(dut.io.WB.notbubble,dut.io.WB.pc.peek().litValue,numeric_width) + RESET
        }else {
            colorChar + s"[WB] No Write Back " + dumpPU.bubbleOrPC(dut.io.WB.notbubble,dut.io.WB.pc.peek().litValue,numeric_width) +RESET
        }
    }
    def dumppipeSate(dut:core.pipelined.TinySOC):String = {
        if(dut.io.pipelinestate.Retired.peek().litToBoolean){
            "[Retired]"
        }else{
            ""
        }
    }
}
class NewSoc extends AnyFreeSpec with Matchers {
    "test soc" in {
        val max_cycle=25
        simulate(new TinySOC(1,"conf/rom.hex")(true)){dut =>
            dumpPU.resetDut(dut)
            for(cnt <- 0 to max_cycle){
                print(dumpPU.dumpPC(dut))
                print(dumpPU.dumpIF(dut))
                print("[InstQueue]")
                print(dumpPU.dumpID(dut))
                print("[reg]")
                print(dumpPU.dumpEXE(dut))
                print("[reg]")
                print(dumpPU.dumpMEM(dut))
                print("[reg]")
                print(dumpPU.dumpWB(dut))
                print(dumpPU.dumppipeSate(dut))
                println()
                dut.clock.step()
            }            
        }
    }
}