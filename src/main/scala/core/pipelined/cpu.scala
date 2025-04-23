package core.pipelined

import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import _root_.circt.stage.ChiselStage
class MiniPU extends Module {
    val io = IO(new Bundle{
        val host = new HostIO
        val icache = Flipped(new mini.CacheIO(64, 64))
        val dcache = Flipped(new mini.CacheIO(64, 64))
    })
    val csr = Module(new mini.CSR(64))
    val regFile = Module(new PipelinedRegFileWithWatchPort)
}

object Try extends App {
    ChiselStage.emitSystemVerilogFile(
        new mini.ImmGenMux(64),
        Array("--target-dir", "build/Try"),
        firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
}
