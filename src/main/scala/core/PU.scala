package core

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage


class PUProbe extends Bundle {
  val memPort = new core.Mem.MemProbeIO
  val dpPort = new core.DataPathProbe
}

class RV64_PU(main_memsize:Int,rom_path:String = "misc/Mem/rom.hex",debug:Boolean=false,reginit:String="conf/reg.ini") extends Module{
  val io = IO(new Bundle{
    val port = if(debug)Some(new PUProbe) else None
  })
  val main_mem = Module(new core.Mem.MainMem(main_memsize)(rom_path)(debug))
  val datapath = Module(new core.DataPath(debug,reginit))
  val ctrl = Module(new core.CtrlU_RV64I )

  if(debug){
    main_mem.io.ProbePort.get <> io.port.get.memPort
    datapath.io.probe.get <> io.port.get.dpPort
  }

  datapath.io.dpIO.memory <> main_mem.io.MemPort
  datapath.io.dpIO.fetch <> main_mem.io.IFPort
  ctrl.io <> datapath.io.dpIO.ctrl
  println("Successfully initial PU")
}

object PU_RV64I_V extends App{
  ChiselStage.emitSystemVerilogFile(
    new RV64_PU(16,"misc/Mem/rom.hex",false),
    Array("--target-dir","build/RV64"),
    firtoolOpts = Array("-disable-all-randomization",
    "-strip-debug-info")
  )
}
