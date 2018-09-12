package io.univalence.aboutfp

import cats.effect.IO
import io.univalence.aboutfp.repository.PersonRepository

class PersonService(repository: PersonRepository) {
  def allPersons: IO[List[Person]]           = repository.allPersons
  def person(id: String): IO[Option[Person]] = repository.person(id)
  def count: IO[Int]                         = repository.count
}
