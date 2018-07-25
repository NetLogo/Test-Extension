enablePlugins(NetLogoExtension)

scalaVersion := "2.12.6"

scalaSource in Compile := baseDirectory.value / "src" / "main"

scalaSource in Test := baseDirectory.value / "src" / "test"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings",
                      "-feature", "-encoding", "us-ascii")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5",
  "org.scalatest"          %% "scalatest"                % "3.0.5"  % "test",
  "org.picocontainer"      % "picocontainer"             % "2.13.6" % "test",
  "org.ow2.asm"            % "asm-all"                   % "5.0.3"  % "test"
)

name := "test-extension"

netLogoVersion      := "6.0.4"
netLogoExtName      := "test"
netLogoClassManager := "org.nlogo.extensions.test.TestExtension"
netLogoTarget       := NetLogoExtension.directoryTarget(baseDirectory.value)

val testingDirectory = settingKey[File]("directory that extension is moved to for testing")
testingDirectory := {
  baseDirectory.value / "extensions" / "test"
}

val setupTests = taskKey[Unit]("setup test directory")
setupTests := {
  IO.createDirectory(testingDirectory.value)
  (packageBin in Compile).value
  val testTarget = NetLogoExtension.directoryTarget(testingDirectory.value)
  testTarget.create(NetLogoExtension.netLogoPackagedFiles.value)
}

test in Test := {
  setupTests.value
  (test in Test).value
  IO.delete(testingDirectory.value)
}
