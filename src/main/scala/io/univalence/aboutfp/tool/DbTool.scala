package io.univalence.aboutfp.tool

import doobie._
import doobie.implicits._
import cats.effect.{ContextShift, IO}
import cats.implicits._
import scala.concurrent.ExecutionContext

object DbTool {
  val dbPort   = 32768
  val database = "business"
  val table    = "person"

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val xa: Transactor[IO] =
    Transactor.fromDriverManager[IO](
      driver = "org.postgresql.Driver",
      url = s"jdbc:postgresql://localhost:$dbPort/$database",
      user = "postgres",
      pass = ""
    )

  def main(args: Array[String]): Unit = {
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
