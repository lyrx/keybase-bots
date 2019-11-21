package com.lyrx.text.processing

import com.lyrx.text.processing.Types.HeaderDetection
import typings.node
import node.{fsMod => fs}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
@JSExportTopLevel("Chunker")
object Main extends Chunker {

  implicit val exc = ExecutionContext.global
  val mayRoot =
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
  val ctxs = Seq(may("satanundischarioti", mayRoot))

  def may(aName: String, base: String) = Book.from(aName, base)
  @JSExport
  def initt() = ctxs.map(chunk(_))

  private def chunk(book: Book) =
    book
      .withMarkdownSections()
      .flatMap(
        b =>{
          //b.sections.map(section => println(section))
          b.writeMarkdownChunks(30)
          .map(b2 => b2.sections.map(section => println(section)))
        }

      )

}
