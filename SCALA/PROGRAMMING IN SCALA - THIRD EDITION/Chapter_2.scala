object SecondChapter {

  def main(args: Array[String]): Unit = {
    println ("Hello World")
    // Basic function calls
    max(3,5)
    display_greetings()

    println("Printing loops with while loops -")
    var i =0
    while(i < args.length){
      println(args(i))
      i += 1
    }

    println("Printing loops with foreach loops -")
    args.foreach((arg: String) => println(arg))

    args.foreach(println)
  }

  def max(x: Int, y: Int): Unit = {
    if(x > y)
      println("Maximum number = "+ (x))
    else
      println("Maximum Number = "+ (y))
  }

  def display_greetings(): Unit =
  {
    println("Hello amita... learning scala.")
  }


}

