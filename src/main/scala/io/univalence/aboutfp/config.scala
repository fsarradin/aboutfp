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
