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



  def writeChunks(max:Int)(implicit executionContext: ExecutionContext)=
    toFiles(
    linesMap,
    context,
    30).
      map(newSections => new Book(
        context = this.context,
        sections = newSections,
        linesMap = this.linesMap
      ))


  def toHTML()(
    implicit ctx: ExecutionContext) =
    Future.sequence(sections.map(section=>{
      sectionToHTML(section).map(
        newPages=>section.copy(pages=newPages)
      )
    })).
      map(newSections => new Book(
        context  = Book.this.context,
        sections = newSections,
        linesMap = Book.this.linesMap
      ))



     // map(_.flatten).
      //map(snippets => snippets)


  /*
    toFiles(
      readStream,
      context
    ).map(_.map(sectionToHTML(_)))
      .map(Future.sequence(_)).
      flatten.map(_.toSeq.flatten)




   */

}
