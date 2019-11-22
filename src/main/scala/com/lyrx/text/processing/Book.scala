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
                      baseOutputDir = s"${Main.output}",
                      markdownSourceOpt = Some(s"${baseDir}/${aName}.md")),
    sections = Seq()
  )
}
class Book(
    val context: Context,
    val sections: Iterable[Section],
    val linesMap:LinesMap = immutable.HashMap()
) extends Chunker {

  def withMarkdownSections()(implicit executionContext: ExecutionContext) =
    context.markdownSourceOpt.map(s => {
      toSections(fs.createReadStream(s),context).
        map(newLinesMap=>new Book(
          context = this.context,
          sections = newLinesMap.keys,
          linesMap = newLinesMap))
    }).getOrElse(Future{this})



  def writeMarkdownChunks(max:Int)(implicit executionContext: ExecutionContext)=
    markDownToFiles(
    linesMap,
    context,
    30).
      map(newSections => new Book(
        context = this.context,
        sections = newSections,
        linesMap = this.linesMap
      ))


  def writeHTMLChunks()(
    implicit ctx: ExecutionContext) =
    Future.sequence(sections.map(section=>{
      sectionToHTML(section,this.context).map(
        newPages=>section.copy(pages=newPages)
      )
    })).
      map(newSections => new Book(
        context  = Book.this.context,
        sections = newSections,
        linesMap = Book.this.linesMap
      ))



}
