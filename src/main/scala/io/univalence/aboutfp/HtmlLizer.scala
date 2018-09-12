package io.univalence.aboutfp

class HtmlLizer(config: Config) {

  def formatPerson(person: Person): String = {
    val agePart =
      person.age.map(a => s" - $a yo").getOrElse("")

    s"""<li>${person.name}$agePart (id=${person.id}) <span style="color: red;">❤</span> ${config.organizationName}</li>"""
  }

  def format(persons: List[Person]): String = {
    s"""<!DOCTYPE html>
        |<html>
        |<head>
        |  <meta charset="UTF-8" />
        |  <title>Hello</title>
        |  <link href="${config.cssFilename}" rel="stylesheet" type="text/css" media="all">
        |</head>
        |<body>
        |  <header>
        |    <p class="title"><strong>Hello</strong></p><h1 class="header-logo"><img height="80" src="${config.logoFilename}"/></h1>
        |  </header>
        |  <main>
        |    <div class="content">
        |      <ul>${persons.map(formatPerson).mkString("\n")}</ul>
        |    </div>
        |  </main>
        |  <footer>
        |    <div>
        |      made with Scala, Doobie, Http4s, Sttp, Circe, Cats, 𝜆, and ❤
        |    </div>
        |  </footer>
        |</body>
        |</html>
        |""".stripMargin
  }

}
