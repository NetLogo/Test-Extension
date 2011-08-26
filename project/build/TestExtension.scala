import sbt._

class TestExtensionProject(info: ProjectInfo) extends DefaultProject(info) {

  override def mainScalaSourcePath = "src"

  override def artifactID = "test"
  override def artifactBaseName = artifactID

  override def packageOptions = super.packageOptions ++ Seq( 
	ManifestAttributes(
		"Manifest-Version" -> "1.0",
		"Extension-Name" -> "test",
		"Class-Manager" -> "UnitTestExtension",
		"NetLogo-Extension-API-Version" -> "5.0"))

  val netlogo = "NetLogo" % "NetLogo" % "5.0beta4" from
    "http://ccl.northwestern.edu/netlogo/5.0beta4/NetLogo.jar"
}

