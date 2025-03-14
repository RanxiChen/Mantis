package tool
import  scala.util.parsing.combinator._
import scala.util.matching.Regex

sealed trait PUPort
case class Mem(val addr:BigInt) extends PUPort{
    override def toString:String = s"Mem(0x${addr.toString(16)})"
}
case class Reg(val n:Int) extends PUPort
case object PC extends PUPort


object MemParser extends RegexParsers {
    override def skipWhitespace = false

    def hexNumber: Parser[BigInt] = """0x[0-9a-fA-F]+""".r ^^ { case s => BigInt(s.substring(2), 16) }
    def term: Parser[BigInt] = hexNumber
    def expr:Parser[BigInt] = term ~ rep(("+" | "-") ~ term) ^^ {
        case first ~ list => list.foldLeft(first) {
            case (x, "+" ~ y) => x + y
            case (x, "-" ~ y) => x - y
            case _ => throw new Exception("Error expr")
        }
    }
    def MemClass:Parser[Mem] = "M" ~ "(" ~> expr <~ ")" ^^ { value =>
        Mem(value)
    }
    def parseMem(s:String):Mem = {
        parse(MemClass,s) match {
            case Success(result, _) => result
            case _ => throw new Exception("Error Mem define")
        }
    }
}

object PUPort {
    def portfromString(s:String):PUPort ={
        if(s == "PC"){
            PC
        }else if(RegAlias.rfalias.contains(s)){
            Reg(RegAlias.rfalias(s))
        }else{
            MemParser.parseMem(s)
        }
    }

    type PUTestValue = Seq[(PUPort,BigInt)]
    type PUTestcase = Map[Int,PUTestValue]
    
    def testSetsfromFile(filename:String):PUTestcase ={
        val filePath = os.pwd /os.RelPath(filename)
        val lines = os.read.lines(filePath)
        var cyclecnt=0
        val res = scala.collection.mutable.Map[Int,PUTestValue]()
        for(line <- lines){
            //println(line)
            if(line.startsWith("@")){
                cyclecnt = line.split(" ")(1).toInt
                res.put(cyclecnt,Seq())
            }else if(line.endsWith(";")){
                var raw = line.stripSuffix(";").split("==").map(_.trim())
                var testcase:(PUPort,BigInt) = (PUPort.portfromString(raw(0)),BigInt(raw(1).substring(2),16))
                var currentList = res(cyclecnt)
                res(cyclecnt) = currentList :+ testcase
            }else{}
        }
        res.toMap
        }
    }



