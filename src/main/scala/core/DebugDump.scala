package core
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
  val MAGENTA = "\u001B[35m"
  val DARK_GRAY = "\u001B[90m"
  val colorbar = Array(
    //BLACK,
    //RED,
    YELLOW,
    GREEN,
    BLUE,
    PURPLE,
    CYAN,
    //BLACK
    //WHITE
  )
  def getcolor(Index:Int): String = {
    if(Index < 0){
      colorbar(0)
    }else{
    colorbar(Index%5)
  }
}
}

