package io.univalence.aboutfp.repository

import java.io.IOException
import java.time.LocalDate

import cats.effect.IO
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import io.univalence.aboutfp.Person
import io.circe._
import io.circe.parser._

class PersonParisOpenDataRepository extends PersonRepository {

  override def allPersons: IO[List[Person]]           = getPersons.map(_.values.toList)
  override def person(id: String): IO[Option[Person]] = getPersons.map(_.get(id))
  override def count: IO[Int]                         = getPersons.map(_.size)

  import com.softwaremill.sttp._

  val uri: Uri =
    uri"https://opendata.paris.fr/api/records/1.0/search/?dataset=les-conseillers-de-paris-de-1977-a-2014&rows=100"

  private def getPersons: IO[Map[String, Person]] =
    getContent
      .map { content =>
        val json = parse(content).getOrElse(Json.Null)
        val records: Seq[Json] =
          json.hcursor
            .downField("records")
            .focus
            .flatMap(_.asArray)
            .getOrElse(Seq.empty)

        val now = LocalDate.now()

        val persons: Seq[Either[DecodingFailure, Person]] =
          for (record <- records)
            yield {
              val cursor       = record.hcursor
              val cursorFields = cursor.downField("fields")

              for {
                id           <- cursor.downField("recordid").as[String]
                lastName     <- cursorFields.downField("nom").as[String]
                firstName    <- cursorFields.downField("prenom").as[String]
                rawBirthDate <- cursorFields.downField("ne_e_le").as[String]
              } yield {
                val birthDate: LocalDate = LocalDate.parse(rawBirthDate)
                val age: Short           = birthDate.until(now).getYears.toShort

                Person(id, s"$firstName $lastName", Some(age))
              }
            }

        persons
          .flatMap(_.toSeq)
          .map(p => p.id -> p)
          .toMap
      }

  private def getContent: IO[String] = {
    implicit val httpBackend: SttpBackend[IO, Nothing] = AsyncHttpClientCatsBackend()
    val backendResource: IO[SttpBackend[IO, Nothing]]  = IO(httpBackend)

    backendResource.bracket { _ =>
      sttp
        .get(uri)
        .send()
        .flatMap { response =>
          val body: Either[Throwable, String] = response.body.left.map(new IOException(_))

          IO.fromEither(body)
        }
    }(backend => IO(backend.close()))
  }

}
