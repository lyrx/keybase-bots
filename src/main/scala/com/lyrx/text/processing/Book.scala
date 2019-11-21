package com.lyrx.text.processing

import Types._

import scala.concurrent.{ExecutionContext, Future}
import typings.node
import node.{fsMod => fs}

import scala.collection.immutable
object Book {

  def from(aName: String, baseDir: String) = new Book(
    context = Context(headerLevel = Main.h,
                      metaData = MetaData(name = aName),
                      outPath = Main.output,
                      markdownSourceOpt = Some(s"${baseDir}/${aName}.md")),
    sections = Seq()
  )
}
class Book(
    val context: Context,
    val sections: Iterable[Section],
    val linesMap:LinesMap = immutable.HashMap()
) extends Chunker {

  def withSections()(implicit executionContext: ExecutionContext) =
    context.markdownSourceOpt.map(s => {
      toSections(fs.createReadStream(s),context).
        map(newLinesMap=>new Book(
          context = this.context,
          sections = this.sections,
          linesMap = this.linesMap))
    }).getOrElse(Future{this})





}
