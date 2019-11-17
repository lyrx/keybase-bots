package com.lyrx.text.processing

import scala.scalajs.js
import typings.node
import node.fsMod.ReadStream
import node.readlineMod.Interface
import node.{fsMod => fs, readlineMod => readline}
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node.NodeJS.ErrnoException

import js.annotation.{JSExport, JSExportTopLevel}
import scala.concurrent
import scala.concurrent.ExecutionContext
@JSExportTopLevel("Chunker")
object Main extends Chunker {


  type HeaderDetection = String => Int

  val may =
    "/Users/alex/git/texte/projects/lyrxgenerator/src/main/resources/books/KarlMay"
  val output = "/Users/alex/output"
  val h: HeaderDetection = (line) => {
    val s = line.trim

    if (s.startsWith("####"))
      4
    else if (s.startsWith("###"))
      3
    else if (s.startsWith("##"))
      2
    else if (s.startsWith("#"))
      1
    else
      0
  }
  @JSExport
  def initt(): Unit = {
    toSections(fs.createReadStream(s"${may}/satanundischarioti.md"))(
      Context(
        headerLevel = h,
        name = "satanundischarioti",
        outPath = output,
        executionContext = ExecutionContext.global
      )).map(m=>println(m.size))(ExecutionContext.global)
  }

}

case class Section(level: Int)

case class Context(
    headerLevel: Main.HeaderDetection,
    name: String,
    outPath: String,
    executionContext: ExecutionContext
)

trait Chunker {

  def read(readStream: ReadStream) = {
    val interface: Interface = readline.createInterface(readStream)
    var seq: Array[String] = Array()
    val promise = concurrent.Promise[Array[String]]()
    interface.on("line", (s) => { seq = (seq :+ s.toString) })
    interface.on("close", (e) => {
      promise.success(seq)
      ()
    })
    promise.future
  }

  def toFile(section: Section, array: Array[String])(implicit ctx: Context) = {}

  def toFiles(readStream: ReadStream)(implicit ctx: Context) = {

    toSections(readStream).map( mm=> {
      mkdirp(s"${ctx.outPath}/${ctx.name}", (e: ErrnoException, m: Made) => {})
    })(ctx.executionContext)
  }

  def toSections(
      readStream: ReadStream
  )(implicit ctx: Context) = {

    val p = concurrent.Promise[Map[Section, Array[String]]]()

    read(readStream).map( lines => {
      var counter = 0;
      p.success(lines.groupBy[Section]((line: String) => {
        if (ctx.headerLevel(line) > 0)
          counter = counter + 1
        Section(counter)
      }))
    })(ctx.executionContext)
    p.future
  }

}
