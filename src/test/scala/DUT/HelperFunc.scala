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
        var src1_source = rs1_source_ID(dut)
        var src2_source = rs2_source_ID(dut)
        val colorChar = getcolor(dut.io.ID.pc.peek().litValue.toInt/4)
        colorChar + s"[ID] src1:0x%016x from ${src1_source} src2:0x%016x from ${src2_source} (bypass_in1:0x%016x,bypass_in2:0x%016x)".format(src1,src2,
        dut.io.ID.rs1_bypass_in.peek().litValue,
        dut.io.ID.rs2_bypass_in.peek().litValue) +
            dumpPU.bubbleOrPC(dut.io.ID.notbubble,dut.io.ID.pc.peek().litValue,numeric_width) + RESET
    }
    def rs1_source_ID(dut:core.pipelined.TinySOC):String = {
        if(dut.io.ID.src1_source.peek().litValue == A_PC.litValue){
            "PC " + "  "
        }else {
            "Reg" + f"${dut.io.ID.inst_rs1.peek().litValue}%02d"
        }
    }
    def rs2_source_ID(dut:core.pipelined.TinySOC):String = {
        if(dut.io.ID.src2_source.peek().litValue == B_RS2.litValue){
            ("Reg"+ f"${dut.io.ID.inst_rs2.peek().litValue}%02d")
        }else if(dut.io.ID.src2_source.peek().litValue == B_IMM.litValue){
            ("Imm" + "  ")
        }else{
            ("PC " + "  ")
        }
    }
    def format_boolean(a:Boolean):String = {
        if(a){
            " true"
        }else{
            "false"
        }
    }
    def dumpID_bypass(dut:core.pipelined.TinySOC,numeric_width:Int = 3) :String = {
        val colorChar = getcolor(dut.io.ID.pc.peek().litValue.toInt/4)
        colorChar + "[ID]" + "src1 0x:%016x".format(dut.io.ID.src1.peek().litValue) + " from " + rs1_source_ID(dut) + " with bypass_able:" + 
        format_boolean(dut.io.bypass.rs1_bypass_able.peek().litToBoolean) + " with bypass data :0x%016x".format(dut.io.ID.rs1_bypass_in.peek().litValue) +
        " src2 0x:%016x".format(dut.io.ID.src2.peek().litValue)  + " from " + rs2_source_ID(dut) + " with bypass_able:" + format_boolean(dut.io.bypass.rs2_bypass_able.peek().litToBoolean) +
        " with bypass data :0x%016x".format(dut.io.ID.rs2_bypass_in.peek().litValue) + dumpPU.bubbleOrPC(dut.io.ID.notbubble,dut.io.ID.pc.peek().litValue,numeric_width) + RESET
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
        colorChar + s"[EXE]%s %016x %016x -> 0x%016x ".
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
                s"[MEM] Write:0x%016x%s%s  to  0x%016x ".
                format(dut.io.MEM.wdata.peek().litValue,
                    dumpPU.mem_sig(dut.io.MEM.sig),
                    dumpPU.mem_width(dut.io.MEM.bfwd),
                    dut.io.MEM.addr.peek().litValue)+ dumpPU.bubbleOrPC(dut.io.MEM.notbubble,dut.io.MEM.pc.peek().litValue,numeric_width) + RESET
        }else{
            colorChar + 
                s"[MEM]  Read:0x%016x%s%s from 0x%016x ".
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
            colorChar + s"[WB]Write 0x%016x to Reg%02d from %s ".
            format(dut.io.WB.rd_data.peek().litValue,
                dut.io.WB.rd_addr.peek().litValue,
                dumpPU.wb_sel(dut.io.WB.wb_sel)) + dumpPU.bubbleOrPC(dut.io.WB.notbubble,dut.io.WB.pc.peek().litValue,numeric_width) + RESET
        }else {
            colorChar + s"[WB]%${16 + 27}s".format(" N o   W r i t e   B a c k   ") + dumpPU.bubbleOrPC(dut.io.WB.notbubble,dut.io.WB.pc.peek().litValue,numeric_width) +RESET
        }
    }
    def dumppipeSate(dut:core.pipelined.TinySOC):String = {
        if(dut.io.pipelinestate.Retired.peek().litToBoolean){
            "[Retired]"
        }else{
            ""
        }
    }
    def testRef(dut:core.pipelined.TinySOC,testSets:tool.PUPort.PUTestValue):Unit = {
        for((port,name) <- testSets){
            port match {
                case tool.Reg(n) => {
                    dut.io.rf.addr.poke(n.U)
                    //dut.io.rf.data.expect(name.U)
                    print("[Reg] %02d:0x%03x\t".format(n,name))
                    print("In fact 0x%03x".format(dut.io.rf.data.peek().litValue))
                }
                case _ => {
                    println("Not support yet")
                }
            }
        } 
    }
    def testSoc(dut:core.pipelined.TinySOC,refFilePath:String = "conf/pipelined.ref",max_cycle:Int=100,after_inst_cnt :Int = 0,numeric_width:Int =3):Unit ={
        val testSets = if(refFilePath==""){
            None
        } else{
            Option(tool.PUPort.testSetsfromFile(refFilePath))
        }
        resetDut(dut)
        var retired_inst_cnt =0
        //var pc_end:BigInt = -1
        var inst_total:BigInt = 0
        var cycle_total:BigInt = 0
        var work:Boolean = true
        for(cnt <- 0 to max_cycle if work ){
            if(after_inst_cnt <= retired_inst_cnt ){

                //if(dut.io.IF.inst.peek().litValue == BitPat.bitPatToUInt(core.Instructions.END).litValue){
                //    pc_end = dut.io.IF.pc.peek().litValue
                //}            
                print(dumpPU.dumpPC(dut,numeric_width))
                print(dumpPU.dumpIF(dut,numeric_width))
                print("[InstQueue]")
                print(dumpPU.dumpID(dut,numeric_width))
                print("[reg]")
                print(dumpPU.dumpEXE(dut,numeric_width))
                print("[reg]")
                print(dumpPU.dumpMEM(dut,numeric_width))
                print("[reg]")
                print(dumpPU.dumpWB(dut,numeric_width))
            }
                //print(dumpPU.dumppipeSate(dut))
            if(dut.io.pipelinestate.Retired.peek().litToBoolean){
                retired_inst_cnt += 1
                print("Inst %03d retired\t".format(retired_inst_cnt))
                if(testSets !=None){
                    if(testSets.get.contains(retired_inst_cnt) && retired_inst_cnt <= testSets.get.keys.max){
                        print("TestRef:")
                        dumpPU.testRef(dut,(testSets.get)(retired_inst_cnt))
                    }
                }
            }
            println()
            //if(dut.io.WB.pc.peek().litValue == pc_end){
            //    inst_total = retired_inst_cnt + 1
            //    cycle_total = dut.io.mcycle.peek().litValue + 1
            //    work = false
            //}
            dut.clock.step()
        }
        //if(pc_end == -1){
        //    println("No END instruction found")
        //}else{
        //    println(s"Total cycle: $cycle_total")
        //    println(s"Total inst: $inst_total")
        //    println(s"CPI: ${BigDecimal(cycle_total) / BigDecimal(inst_total)}")
            //println(pc_end.toString(16))
        //}
    }
    def peek(dut:core.pipelined.TinySOC,max_cycle:Int = 1000,numeric_width:Int=3,start_cycle:Int=0,end_cycle:Int = -1):Unit = {
        var real_end_cycle = (if(end_cycle == -1){
            max_cycle
        }else {
            end_cycle
        })
        resetDut(dut)
        var retired_inst_cnt =0
        for(cycle_cnt <- 0 until max_cycle){
            if(cycle_cnt >= start_cycle && cycle_cnt <= real_end_cycle){
                print(dumpPC(dut))
                /*val color_of_PC_IF = getcolor(dut.io.IF.pc.peek().litValue.toInt/4)
                print(
                    color_of_PC_IF + s"0x%0${numeric_width}x ->pcReg with flush:".format(dut.io.pc.pc_after_flush.peek().litValue) + (
                        if(dut.io.pc.flush.peek().litToBoolean) {
                            " true "
                        } else {
                            "false "
                        }
                    ) + "with clr:" + (
                        if(dut.io.pc.clr.peek().litToBoolean) {
                            " true "
                        } else {
                            "false "
                        }
                    ) + s"with value:0x%0${numeric_width}x  ".format(dut.io.pc.pcReg.peek().litValue) + RESET
                )*/
                print(dumpIF(dut,numeric_width))
                print("[InstQueue]")
                /*print(
                    getcolor(dut.io.ID.pc.peek().litValue.toInt/4) + "[ID]" + bubbleOrPC(dut.io.ID.notbubble,dut.io.ID.pc.peek().litValue,numeric_width) + " " +
                    "pc_sel:" + dut.io.ID.pc_sel.peek().litValue  + " "  +RESET
                )*/
                print(dumpID_bypass(dut,numeric_width))
                print("[reg]")
                /*print(
                    getcolor(dut.io.EXE.pc.peek().litValue.toInt/4) + "[EXE]" + bubbleOrPC(dut.io.EXE.notbubble,dut.io.EXE.pc.peek().litValue,numeric_width) + " " +
                    "pc_sel: " + dut.io.EXE.pc_sel.peek().litValue  + RESET
                )*/
                print(dumpEXE(dut,numeric_width))
                print("[reg]")
                print(
                    getcolor(dut.io.MEM.pc.peek().litValue.toInt/4) + "[MEM]" + bubbleOrPC(dut.io.MEM.notbubble,dut.io.MEM.pc.peek().litValue,numeric_width)  + RESET
                )
                print("[reg]")
                print(
                    getcolor(dut.io.WB.pc.peek().litValue.toInt/4) + "[WB]" + bubbleOrPC(dut.io.WB.notbubble,dut.io.WB.pc.peek().litValue,numeric_width)  + RESET
                )
                println()
            }
            dut.clock.step()
        }
    }
}
