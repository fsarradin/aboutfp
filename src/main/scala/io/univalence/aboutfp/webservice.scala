package io.univalence.aboutfp

import java.io.File
import java.nio.file
import java.nio.file.FileSystems
import java.util.concurrent.Executors

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.headers._
import org.http4s.HttpRoutes

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContextExecutorService

class PersonWebService(service: PersonService, config: Config) {

  lazy val route: HttpRoutes[IO] =
    HttpRoutes
      .of[IO] {
        case GET -> Root =>
          val persons: IO[String] = service.allPersons.map(htmlLizer.format)
          Ok(persons).map(_.withContentType(`Content-Type`(MediaType.text.html)))

        case GET -> Root / "person" :? FormatParamMatcher(format) =>
          val (maybeFormatter, contentType) = parseFormat(format)
          val response: Option[IO[Response[IO]]] =
            maybeFormatter
              .map { (formatter: List[Person] => String) =>
                val persons: IO[String] = service.allPersons.map(formatter)
                Ok(persons).map(_.withContentType(contentType))
              }

          response.getOrElse(BadRequest(s"Unknown format: $format"))

        case GET -> Root / "person" =>
          val persons = service.allPersons.map(toJson)
          Ok(persons).map(_.withContentType(`Content-Type`(MediaType.application.json)))

        case GET -> Root / "person" / "_count" =>
          val count = service.count.map(c => s"""{"count": $c}""")
          Ok(count).map(_.withContentType(`Content-Type`(MediaType.application.json)))

        case GET -> Root / "person" / id =>
          val personIO: IO[Option[Person]] = service.person(id)

          val result: IO[Response[IO]] =
            personIO.flatMap { maybePerson =>
              val value: IO[Response[IO]] = maybePerson
                .map(p => Ok(toJson(p)).map(_.withContentType(`Content-Type`(MediaType.application.json))))
                .getOrElse(NotFound(s"unknown person id $id"))
              value
            }

          result

        case request @ GET -> "assets" /: asset =>
          val assetFile = assetPath(asset.toList)
          StaticFile
            .fromFile(assetFile, blockingEc, Some(request))
            .getOrElseF(NotFound())

        case badRequest =>
          NotFound(s"Not Found ${badRequest.uri}")
      }

  val htmlLizer = new HtmlLizer(config)

  val baseDir: file.Path = FileSystems.getDefault.getPath("src", "main", "resources", "static", "assets")

  val blockingEc: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private def parseFormat(format: String): (Option[List[Person] => String], `Content-Type`) =
    format.toLowerCase() match {
      case "json" => (Some(toJson), `Content-Type`(MediaType.application.json))
      case "csv"  => (Some(toCsv), `Content-Type`(MediaType.text.plain))
      case _      => (None, `Content-Type`(MediaType.text.plain))
    }

  private def toJson(p: Person): String =
    s"""{ "id": "${p.id}", "name": "${p.name}", "age": ${p.age.map(_.toString).getOrElse("null")} }"""

  private def toJson(persons: List[Person]): String =
    persons
      .map(toJson)
      .mkString("[", ", ", "]")

  private def toCsv(persons: List[Person]): String =
    ("ID,NAME,AGE\n"
      + persons
        .map(p => s"""${p.id},"${p.name.replace("\"", "\"\"")}",${p.age.map(_.toString).getOrElse("NULL")}""")
        .mkString("\n"))

  def assetPath(paths: List[String]): File =
    paths.foldLeft(baseDir) { case (dir, f) => dir.resolve(f) }.toAbsolutePath.toFile

  object FormatParamMatcher extends QueryParamDecoderMatcher[String]("format")

}
