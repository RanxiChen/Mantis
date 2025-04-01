package tool
import scala.collection.mutable._
object MemHex {
    def hexmapfromfile(path:String = "conf/rom.map"):Map[Int,Int] ={
        val mapFile = os.pwd / os.RelPath(path)
        val lines = os.read.lines(mapFile).filter(_.trim.nonEmpty)
        var hexMap:Map[Int,Int] = Map()
        for(line <- lines.filterNot(_.contains("//"))){
            //println(line)
            var raw = line.split(":")
            var mapkey = BigInt(raw(0).stripPrefix("0x"),16).toInt
            var mapvalue = BigInt(raw(1).stripPrefix("0x"),16).toInt
            hexMap = hexMap ++ Map(mapkey -> mapvalue)
        }
        hexMap
    }
}
// Process inst
object ProcessHexInst {
    def hexInsttoReadableString(inst:String):String ={
        val hexString = inst.grouped(2).toSeq.reverse.mkString
        val inst_raw = os.proc("/home/chen/Tool/capstone/bin/cstool","riscv64",hexString).call().chunks(0) match {
            case Left(value) => value
            case _ => "error"
        }
        inst_raw.toString.trim
        //inst_raw.toString.filterNot(char => char.isDigit || char.isSpaceChar).trim.replace("\t"," ")       
    }
    val disassemble:(String => String) = {inst => hexInsttoReadableString(inst)}
    val alias:(String => String) = { name => "Reg" + "%02d".format(RegAlias.rfalias(name))}
}

object MemHexTest extends App {
    val hexmap = MemHex.hexmapfromfile("unittest/RV64/test03.map")
    println(hexmap)
}
object disassemble extends App {
    println(ProcessHexInst.hexInsttoReadableString(args(1)))
}