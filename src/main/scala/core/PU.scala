package core

import chisel3._

class ProbeIO extends Bundle{
  ???
}

class single_cycleRV64Ip_PU extends Module{
  val main_mem = Module(new core.Mem.MainMem(2)(true))
  val rf = Module(new core.RegFile.RegFile(true))
  
}
