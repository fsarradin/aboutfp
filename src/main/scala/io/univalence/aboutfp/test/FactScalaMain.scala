package io.univalence.aboutfp.test
import scala.collection.immutable.ListMap

object FactScalaMain {

  // Approche impérative
  def fact_ip(n: Int): Int = {
    var result: Int = 1

    for (i <- 1 to n) {
      result *= i
    }

    result
  }

  // Approche récursive
  def fact_fp1(n: Int): Int =
    if (n <= 1) 1
    else n * fact_fp1(n - 1)

  // Utilisation de foldLeft
  def fact_fp2(n: Int): Int =
    (1 to n).foldLeft(1)(_ * _)

  // Utilisation de product
  def fact_fp3(n: Int): Int =
    (1 to n).toList.product

  // Utilisation de product avec un Iterator (for free)
  def fact_fp4(n: Int): Int =
    (1 to n).product

  // flux de résultats de factoriel (corécursion ou codata)
  def fact_fp5(n: Int): Int =
    factStream.drop(n).head

  val factStream: Stream[Int] =
    Stream.from(1).scanLeft(1)(_ * _)

  // ================================================================================

  def main(args: Array[String]): Unit = {
    val functions =
      List(
        "ip"  -> fact_ip _,
        "fp1" -> fact_fp1 _,
        "fp2" -> fact_fp2 _,
        "fp3" -> fact_fp3 _,
        "fp4" -> fact_fp4 _,
        "fp5" -> fact_fp5 _
      )

    displayResult(functions, n = 10, padding = 10)

    println()
    println("fact stream:" + factStream.take(10).mkString(", "))
  }

  def displayResult(functions: List[(String, Int => Int)], n: Int, padding: Int): Unit = {
    val header =
      functions
        .map(_._1)
        .map(s => leftPad(s, ' ', padding))
        .foldLeft("")((acc, s) => acc + s)

    println(rightPad("", ' ', padding) + header)

    (0 to n).foreach(i => println(getResult(i, functions.map(_._2), padding)))
  }

  def getResult(i: Int, functions: Iterable[Int => Int], padding: Int): String =
    functions
      .map(f => leftPad(f(i).toString, ' ', padding))
      .mkString(rightPad(i + ":", ' ', padding), "", "")

  def leftPad(s: String, c: Char, n: Int): String = {
    val count = Math.max(0, n - s.length)

    List.fill(count)(c).mkString + s
  }

  def rightPad(s: String, c: Char, n: Int): String =
    s.padTo(n, c)

}
