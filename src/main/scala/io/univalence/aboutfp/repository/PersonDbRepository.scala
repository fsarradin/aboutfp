package io.univalence.aboutfp.repository

import cats.effect.IO
import doobie._
import doobie.implicits._

import io.univalence.aboutfp.Person

class PersonDbRepository(xa: Transactor[IO]) extends PersonRepository {
  override def allPersons: IO[List[Person]] =
    sql"SELECT id, name, age FROM person"
      .query[Person]
      .to[List]
      .transact(xa)

  override def person(id: String): IO[Option[Person]] =
    sql"SELECT id, name, age FROM person WHERE id = ${id.toInt}"
      .query[Person]
      .option
      .transact(xa)

  override def count: IO[Int] =
    sql"SELECT COUNT(id) FROM person"
      .query[Int]
      .unique
      .transact(xa)
}
