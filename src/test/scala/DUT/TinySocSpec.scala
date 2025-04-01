package core.pipelined

import chisel3._
import chisel3.experimental.BundleLiterals._
import core.Signal._

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import core.Colors._
import chiseltest._
import chisel3.experimental.BundleLiterals._
//import chisel3.simulator.EphemeralSimulator._
class TinySocSpec extends AnyFreeSpec with Matchers with ChiselScalatestTester {
    //accept rom and run
    "test one Tiny Soc but 5 stage pipelined" in {
        val max_cycle=100
        test(new TinySOC(1,"conf/rom.hex")(true) ).withAnnotations(Seq(VerilatorBackendAnnotation,WriteVcdAnnotation)){ dut =>
            //reset
            val testSets = tool.PUPort.testSetsfromFile("conf/pipelined.ref")
            dut.reset.poke(true.B)
            dut.clock.step()
            dut.reset.poke(false.B)
            println("Run after reset")
            var Instcnt = 0
            for(cnt <- 0 to max_cycle){
                var colorcnt = cnt
                print(f"[${dut.io.mcycle.peek().litValue}%016x]")
                //IF
                val PC = dut.io.IF.pc.peek().litValue
                val inst_raw ="%08x".format(dut.io.IF.inst.peek().litValue)
                //val inst = tool.ProcessHexInst.hexInsttoReadableString(inst_raw)
                print(f"${getcolor(colorcnt)}[IF] from 0x${PC}%016x get Inst: ${inst_raw}${RESET}[InstQueue]")
                //ID
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
                print(f"${getcolor(colorcnt-1)}[ID] src1:0x${src1}%016x from ${src1_source} src2:0x${src2}%016x from ${src2_source}${RESET}[reg]")
                //EXE
                var op:String  = {
                    dut.io.EXE.alu_op.peek().litValue.toInt match {
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
                val alu_res = dut.io.EXE.alu_res.peek().litValue
                print(f"${getcolor(colorcnt-2)}[EXE] op :${op},alu_res:0x${alu_res}%016x${RESET}[reg]")
                //MEM
                var mem_sig = dut.io.MEM.sig.peek().litToBoolean match {
                    case true => " S"
                    case false => " U"
                }
                var mem_width = {
                    dut.io.MEM.bfwd.peek().litValue.toInt match {
                        case 0 => "B"
                        case 1 => "H"
                        case 2 => "W"
                        case 4 => "D"
                        case _ => "X"
                    }
                }
                if(dut.io.MEM.WE.peek().litToBoolean){
                    print(f"${getcolor(colorcnt-3)}[MEM] Write:0x${dut.io.MEM.wdata.peek().litValue}%016x${mem_sig}${mem_width} to addr:0x${dut.io.MEM.addr.peek().litValue}%016x${RESET}[reg]")
                }else {
                    print(f"${getcolor(colorcnt-3)}[MEM]  Read:0x${dut.io.MEM.rdata.peek().litValue}%016x${mem_sig}${mem_width} from addr:0x${dut.io.MEM.addr.peek().litValue}%016x${RESET}[reg]")
                }
                //WB
                var wb_sel = {
                    dut.io.WB.wb_sel.peek().litValue.toInt match {
                        case 0 => "alu"
                        case 1 => "imm"
                        case 2 => "mem"
                        case 3 => "pc4"
                        case _ => "XXX"
                    }
                }
                var rd = dut.io.WB.rd_addr.peek().litValue
                var rd_data = dut.io.WB.rd_data.peek().litValue
                if(dut.io.WB.WriteEnable.peek().litToBoolean){
                    print(f"${getcolor(colorcnt-4)}[WB] Write:0x${rd_data}%016x to Reg${rd}%02d from ${wb_sel}${RESET}[reg]")
                }else{
                    print(f"${getcolor(colorcnt-4)}[WB] No WB:0x${rd_data}%016x to Reg${rd}%02d from ${wb_sel}${RESET}[reg]")
                }
                //println()
                //Verify
                //Jus compute, no memory
                //Inst will retire at WB stage
                if(dut.io.pipelinestate.Retired.peekBoolean()){
                    Instcnt += 1
                    print(f"${getcolor(colorcnt-5)}[Inst ${Instcnt}%3d Retired]${RESET}")
                }else{
                    print(f"${getcolor(colorcnt-5)}[${"No Inst retired"}%16s]${RESET}")
                }
                if(testSets.contains(Instcnt) && Instcnt <= testSets.keys.max){
                    for((port,name) <- testSets(Instcnt)){
                        port match {
                            case tool.Reg(n) => {
                                dut.io.rf.addr.poke(n.U)
                                dut.io.rf.data.expect(name.U)
                                //print(f"Reg${dut.io.rf.addr.peek().litValue}%02d is 0x${dut.io.rf.data.peek().litValue}%016x")
                            }
                            case _ => {
                                print("[Unexpected] some test ref")
                            }
                        }
                    }
                }
                println()
                dut.clock.step()
            }
            println("run ends")
        }
    }
}