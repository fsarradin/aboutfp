package io.univalence.aboutfp.tool

import doobie._
import doobie.implicits._
import cats.effect.IO
import cats.implicits._

object DbMain {
  val dbPort   = 32768
  val database = "business"
  val table    = "person"

  def main(args: Array[String]): Unit = {
    val xa =
      Transactor.fromDriverManager[IO](
        driver = "org.postgresql.Driver",
        url = s"jdbc:postgresql://localhost:$dbPort/$database",
        user = "postgres",
        pass = ""
      )

    initDb(xa)
  }

  def initDb(xa: Transactor[IO]): Unit = {
    val result =
      for {
        _ <- dropTable(table)
        _ <- createPersonTable
        _ <- insertPersons(
              ("Alice", Some(24)),
              ("Bob", None),
              ("John", Some(32)),
              ("Jack", Some(42)),
              ("Betty", Some(23)),
              ("Carrie", None)
            )
      } yield ()

    result
      .transact(xa)
      .unsafeRunSync()
  }

  def dropTable(table: String): ConnectionIO[Int] =
    Fragment.const(s"DROP TABLE IF EXISTS $table").update.run

  val createPersonTable: ConnectionIO[Int] =
    Fragment.const(s""" CREATE TABLE $table (
      id   SERIAL,
      name VARCHAR NOT NULL UNIQUE,
      age  SMALLINT
    )""").update.run

  def insertPersons(infos: (String, Option[Short])*): ConnectionIO[Int] = {
    val query = s"INSERT INTO $table (name, age) VALUES (?, ?)"
    Update[(String, Option[Short])](query).updateMany(infos.toList)
  }

}
