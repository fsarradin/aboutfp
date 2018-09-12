package io.univalence.aboutfp.tool
import io.univalence.aboutfp.repository.PersonParisOpenDataRepository

object ParisOpenDataMain {

  def main(args: Array[String]): Unit = {
    val repository = new PersonParisOpenDataRepository

    println(repository.allPersons.unsafeRunSync())
  }

}
