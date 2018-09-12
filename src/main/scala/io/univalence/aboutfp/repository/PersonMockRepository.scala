package io.univalence.aboutfp.repository

import cats.effect.IO

import io.univalence.aboutfp.Person

class PersonMockRepository(persons: List[Person]) extends PersonRepository {
  override def allPersons: IO[List[Person]]           = IO(persons)
  override def person(id: String): IO[Option[Person]] = IO(persons.find(p => p.id == id))
  override def count: IO[Int]                         = IO(persons.size)
}
