package io.univalence.aboutfp

import cats.effect._
import cats.implicits._
import io.univalence.aboutfp.repository._
import org.http4s.server.blaze._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    val config     = BnppConfig
    val repository = new PersonMockRepository(persons)
    val service    = new PersonService(repository)
    val webService = new PersonWebService(service, config).webService

    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(webService, "/")
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

  val persons =
    List(Person(
           id = "0",
           name = "Jon",
           age = Some(32)
         ),
         Person(
           id = "1",
           name = "Mary",
           age = None
         ))

}