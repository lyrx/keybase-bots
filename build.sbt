import org.scalajs.core.tools.linker.ModuleInitializer

enablePlugins(ScalaJSPlugin,ScalaJSBundlerPlugin)

name := "mybot"
version := "0.0.1"
scalaVersion := "2.12.8"



scalaJSUseMainModuleInitializer := true

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

libraryDependencies ++= Seq(
  ScalablyTyped.D.d3,
  ScalablyTyped.N.node
)


npmDependencies in Compile ++= Seq(
  "mathjs" -> "6.2.3",
  "keybase-bot" -> "3.0.2"

)





