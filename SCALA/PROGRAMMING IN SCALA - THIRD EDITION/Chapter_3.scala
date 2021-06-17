import scala.collection.mutable

object Chapter_3 {
  def main(args: Array[String]): Unit = {
    val greetStrings : Array[String] = new Array[String](3)
    greetStrings(0) = "Hello"
    greetStrings(1) = ", "
    greetStrings(2) = "world!\n"
    for (i <- 0 to 2)
      print(greetStrings(i))

    var numNames = Array("zero", "one", "two")
    numNames(0) = "Three"
    for (i <- 0 to 2)
      println(numNames(i))

    //val mylist = List(1,2,3,4,4,5)
    val secondList = List(9,8,7,10)
    /*
    :: = This is pronounced as cons
    Cons prepends a new element to the beginning of an existing list and returns
    the resulting list.
     */
    val mylist = 31 :: secondList
    println(mylist)

    // Short hand way to specify an empty list
    val newList = Nil
    val newlist1 = 1 :: 2 :: 3 :: Nil
    println(newlist1)

    /*
    All the list functions
     */
    // initialize empty list
    val empty_list = List()

    // Create new list
    val first_new_list = List("one","second", "third")
    println(first_new_list)

    // concatenate two lists
    val first_list = List("beautiful", "day", "flowers", "candles", "programming")
    val second_list = List("Sunny", "weather", "yoga", "scala", "python")
    val concatenated_list = first_list ::: second_list
    println("the concatenated list - "+concatenated_list)

    // print the element at the index=3
    println("third element from concatenated list - "+concatenated_list(3))

    // List functions
    println("words with length = 3 in the list - "+concatenated_list.count(s => s.length == 3))
    println("Returns the list without its first 5 elements - "+concatenated_list.drop(2))
    println("checks if 'day' is present in the list - "+concatenated_list.exists(s => s == "day"))
    println("returns the elements which have the length 4 - "+concatenated_list.filter(s => s.length == 6))
    println("check which words end with 's' - "+concatenated_list.forall(s => s.endsWith("s")))
    println("all elements - "+concatenated_list.foreach(s => println(s)))
    println("all elements - "+concatenated_list.foreach(print))
    println("first element of list -"+concatenated_list.head)
    println("returns everything except the last element - "+concatenated_list.init)
    println("Returns the length of the list -"+concatenated_list.length)
    println("Adds 'something' at the end of the string - "+concatenated_list.map(s => s+"-something"))
    //println("sorting - "+concatenated_list.sortBy( (s,t) => s.charAt(0).toUpper < t.charAt(0).toUpper ))
    println("Tail of the list -"+concatenated_list.tail)

    /* Tuples */
    val new_tuple = (90, "Amita")
    println(new_tuple._1)
    println(new_tuple._2)

    /* Set */
    var my_sets = Set("Amita", "Ashok")
    my_sets += "Dhainje"
    println(my_sets)
    println("Element checking in sets -"+my_sets.contains("Mudra"))

    /* Mutable Set */
    val new_sets = mutable.Set("Amita", "Ashok")
    my_sets += "Dhainje"
    println(new_sets)

    /* Maps */
    val treasureMap = mutable.Map[Int, String]()
    treasureMap += (1 -> "Go to island.")
    treasureMap += (2 -> "Find big X on ground.")
    treasureMap += (3 -> "Dig.")
    println(treasureMap)

    var new_map = Map( 1 -> "One", 2 -> "Two", 3 -> "Three")
    new_map += (4 -> "four")
    println(new_map)

  }
}