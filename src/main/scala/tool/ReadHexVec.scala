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
object MemHexTest extends App {
    val hexmap = MemHex.hexmapfromfile("unittest/RV64/test03.map")
    println(hexmap)
}