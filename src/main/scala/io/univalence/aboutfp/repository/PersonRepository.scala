package io.univalence.aboutfp.repository

import cats.effect.IO

import io.univalence.aboutfp.Person

trait PersonRepository {
  def allPersons: IO[List[Person]]
  def person(id: String): IO[Option[Person]]
  def count: IO[Int]
}
