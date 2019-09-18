package io.univalence.aboutfp

import cats.effect._
import cats.implicits._
import io.univalence.aboutfp.repository._
import io.univalence.aboutfp.tool.DbTool
import org.http4s.server.blaze._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val config     = SgConfig
    val repository = new PersonMockRepository(persons)
    val service    = new PersonService(repository)
    val webService = new PersonWebService(service, config)

    BlazeBuilder[IO]
      .bindHttp(9080, "localhost")
      .mountService(webService.route, "/")
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
