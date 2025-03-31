package tool
import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._


object Colors {
  val RESET = "\u001B[0m"
  val BLACK = "\u001B[30m"
  val RED = "\u001B[31m"
  val GREEN = "\u001B[32m"
  val YELLOW = "\u001B[33m"
  val BLUE = "\u001B[34m"
  val PURPLE = "\u001B[35m"
  val CYAN = "\u001B[36m"
  val WHITE = "\u001B[37m"
  val colorbar = Array(
    //BLACK,
    //RED,
    GREEN,
    YELLOW,
    BLUE,
    PURPLE,
    CYAN,
    WHITE
  )
}

// 使用示例
object ColorfulText {
  def main(args: Array[String]): Unit = {
    import Colors._
    for ( i <- 0 until 12){
        val color = colorbar(i % colorbar.length)
        println(s"${color}This is a colorful text!${RESET}")
    }
  }
}

object pipelinedPUPuts {
    def ReadWRiteString(signal:Bool):String = {
        if(signal.litToBoolean){
            "Write"
        }else{
            " Read"
        }
    }
    
}