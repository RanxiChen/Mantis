package tool

object HexProcess {
  def generateSeqHex(HexPath:String = "misc/Mem/rom.hex") :Unit={
    val data = (0 to 255).grouped(4).map(_.reverse.map( n=> f"$n%02x").reduce(_+_)).mkString("\n")
    val destrel = os.RelPath(HexPath)
    val dest = os.pwd / destrel
    os.write.over(dest,data)
  }
  def generateUsefulHex():Array[String]={
    (0 to 255).grouped(4).map(_.reverse.map( n=> f"$n%02x").reduce(_+_)).toArray
  }
}

object MemInit extends App {
  HexProcess.generateSeqHex()
}



