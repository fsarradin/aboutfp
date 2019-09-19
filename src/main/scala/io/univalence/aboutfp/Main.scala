package io.univalence.aboutfp

import cats.effect._
import cats.implicits._
import io.univalence.aboutfp.repository._
import io.univalence.aboutfp.tool.DbTool
import cats.implicits._
import org.http4s.syntax._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val config     = SgConfig
    val repository = new PersonMockRepository(persons)
    val service    = new PersonService(repository)
    val webService = new PersonWebService(service, config)

    BlazeServerBuilder[IO]
      .bindHttp(9080, "localhost")
      .withHttpApp(Router("/" -> webService.route).orNotFound)
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
