package com.lyrx.text.processing

import com.lyrx.text.processing.Types.HeaderDetection
import typings.node
import node.{fsMod => fs}

import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
@JSExportTopLevel("Chunker")
object Main extends Chunker {


  implicit val exc = ExecutionContext.global
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
    val ctxs = Seq(Context(
      headerLevel = h,
      metaData = MetaData(name = "satanundischarioti"),
      outPath = output
    ))

    ctxs.map(chunk(_))

  }

  private def chunk(ctx: Context) = {
    toFiles(readStream = fs.createReadStream(
      s"${may}/satanundischarioti.md"),
      actx = ctx).
      map(
        sections => sections.foreach(section => println(section))
      )
  }
}
