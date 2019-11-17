import org.scalablytyped.sbt.ScalablyTypedPlugin.autoImport

enablePlugins(ScalaJSPlugin)

name := "lyrxplayer"
version := "0.0.1"
scalaVersion := "2.12.8"


scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

libraryDependencies ++= Seq(
  ScalablyTyped.N.node,
  ScalablyTyped.M.mkdirp
  //, ScalablyTyped.F.fs
)









