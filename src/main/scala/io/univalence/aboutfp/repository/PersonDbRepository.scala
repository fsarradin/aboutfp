package io.univalence.aboutfp.repository

import cats.effect.IO
import doobie._
import doobie.implicits._
import io.univalence.aboutfp.Person
import scala.util.Try

class PersonDbRepository(xa: Transactor[IO]) extends PersonRepository {
  override def allPersons: IO[List[Person]] =
    sql"SELECT id, name, age FROM person"
      .query[Person]
      .to[List]
      .transact(xa)

  override def person(id: String): IO[Option[Person]] = {
    val maybeId: Option[Int] = Try { id.toInt }.toOption

    if (maybeId.isDefined)
      sql"SELECT id, name, age FROM person WHERE id = ${maybeId.getOrElse(0)}"
        .query[Person]
        .option
        .transact(xa)
    else
      IO(None)
  }

  override def count: IO[Int] =
    sql"SELECT COUNT(id) FROM person"
      .query[Int]
      .unique
      .transact(xa)
}
