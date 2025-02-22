package core.PU

from core.InstructionMem import InstrMem
from core.RegFile import RegFile
from core.DataMem import DataMem

import chisel3._

/**PU for processing unit
 * will be used to generate one "core"
 * I mean , such as one single RV64gc core
 */


class PU extends Module{
  val PC = RegInit(0.U(32.W))
  val InstrMem = Module(new InstrMem)
  val RegFile = Module(new RegFile)
  val DataMem = Module(new DataMem)
}
