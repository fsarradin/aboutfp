package io.univalence.aboutfp

trait Config {
  def organizationName: String
  def cssFilename: String
  def logoFilename: String
}

case object BnppConfig extends Config {
  val organizationName = "BNP Paribas"
  val cssFilename      = "/assets/stylesheet/bnppstyle.css"
  val logoFilename     = "/assets/image/BNPP_logo.png"
}

case object SgConfig extends Config {
  val organizationName = "Société Générale"
  val cssFilename      = "/assets/stylesheet/sgstyle.css"
  val logoFilename     = "/assets/image/SG_logo.png"
}

case object UpnConfig extends Config {
  val organizationName = "Université Paris Nanterre"
  val cssFilename      = "/assets/stylesheet/upnstyle.css"
  val logoFilename     = "/assets/image/UPN_logo.png"
}
