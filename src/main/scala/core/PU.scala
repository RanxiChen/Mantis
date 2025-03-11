package core

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage


class PUProbeIO extends Bundle { 
  val rfPort = new (core.RFProbeIO)
  val memPort = new core.Mem.MemProbeIO
}

class single_cycleRV64Ip_PU(main_memsize:Int,rom_path:String = "misc/Mem/rom.hex",debug:Boolean=false) extends Module{
  val io = IO(new Bundle{
    val port = if(debug)Some(new PUProbeIO) else None
  })
  val main_mem = Module(new core.Mem.MainMem(main_memsize)(rom_path)(debug))
  val datapath = Module(new core.DataPath_single(debug))
  val ctrl = Module(new core.CtrlU_RV64I )

  if(debug){
    main_mem.io.ProbePort.get <> io.port.get.memPort
    datapath.io.rfprobe.get <> io.port.get.rfPort
  }

  datapath.io.memory <> main_mem.io.MemPort
  datapath.io.fetch <> main_mem.io.IFPort
  ctrl.io <> datapath.io.ctrl
}

object PU_RV64I_V extends App{
  ChiselStage.emitSystemVerilogFile(
    new single_cycleRV64Ip_PU(1),
    Array("--target-dir","build/RV64I"),
    firtoolOpts = Array("-disable-all-randomization",
    "-strip-debug-info")
  )
}
