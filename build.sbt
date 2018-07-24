enablePlugins(NetLogoExtension)

scalaVersion := "2.12.6"

scalaSource in Compile := baseDirectory.value / "src" / "main"

scalaSource in Test := baseDirectory.value / "src" / "test"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings",
                      "-feature", "-encoding", "us-ascii")

netLogoVersion := "6.0.4"

netLogoClassManager := "TestExtension"

netLogoTarget := NetLogoExtension.directoryTarget(baseDirectory.value)

test in Test := {
  (test in Test).value
}
