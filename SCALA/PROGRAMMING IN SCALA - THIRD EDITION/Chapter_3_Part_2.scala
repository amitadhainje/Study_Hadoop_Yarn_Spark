import scala.io.Source
import scala.reflect.io.File

object Chapter_3_Part_2 {
  def main(args: Array[String]): Unit = {
    val root_dir = File(".").toAbsolute
    val filename = root_dir + "\\src\\main\\scala\\sample.txt"
    println("The filename -" + filename)

    def widthOfLength(s: String) = s.length.toString.length

    val lines = Source.fromFile(filename).getLines().toList
    val longestLine = lines.reduceLeft(
      (a, b) => if (a.length > b.length) a else b
    )
    val maxWidth = widthOfLength(longestLine)

    /*for (line <- Source.fromFile(filename).getLines())
        println(line.length + " " + line)
    }*/

    for (line <- lines) {
      val numSpaces = maxWidth - widthOfLength(line)
      val padding = " " * numSpaces
      println(padding + line.length + " | " + line)
    }
  }
}
