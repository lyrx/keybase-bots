package com.lyrx.text.processing

import typings.node.{Buffer, process, fsMod => fs, readlineMod => readline}
import typings.node.NodeJS.{ErrnoException, WritableStream}
import typings.node.fsMod.ReadStream
import typings.node.readlineMod.Interface

import scala.scalajs.js
import js.annotation.{JSExport, JSExportTopLevel}
import js.|
@JSExportTopLevel("Chunker")
object Main extends Chunker {

  type ArrayGet = Array[String] => Unit
  type HeaderLevel = String => Int


  val may = "/Users/alex/git/texte/projects/lyrxgenerator/src/main/resources/books/KarlMay"

  implicit val ctx:Context = Context((line) => {
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
  })


  @JSExport
  def initt(): Unit = {
    toSections(fs.createReadStream(s"${may}/satanundischarioti.md"),
         //  _.foreach(println)
         map => {println(map.size)})
  }

}

case class Section(level: Int)

case class Context(headerLevel: Main.HeaderLevel)


trait Chunker {

  def read(readStream: ReadStream, onClosed: Main.ArrayGet) = {
    val interface: Interface = readline.createInterface(readStream)
    var seq: Array[String] = Array()
    interface.on("line", (s) => { seq = (seq :+ s.toString) })
    interface.on("close", (e) => onClosed(seq))
  }

  def toSections(
      readStream: ReadStream,
      cb: Map[Section, Array[String]] => Unit
  )(implicit ctx: Context) =
    read(readStream, lines => {
      var counter = 0;
      cb(lines.groupBy[Section]((line: String) => {
        if (ctx.headerLevel(line) > 0)
          counter = counter + 1
        Section(counter)
      }))
    })

}
