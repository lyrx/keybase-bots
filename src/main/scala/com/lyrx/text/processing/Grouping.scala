package com.lyrx.text.processing

import com.lyrx.text.processing.Types.{Lines, LinesMap, Page, PageMap, ParMap}
import typings.mkdirp.mkdirpMod.Made
import typings.node.NodeJS.ErrnoException
import typings.node.fsMod.ReadStream
import typings.node.readlineMod.Interface
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import typings.node
import node.{fsMod => fs, readlineMod => readline}
trait Grouping {

  def toPars(lines: Lines): ParMap = {
    var counter = 0
    lines.groupBy[Int]((line: String) => {
      if (line.trim.length == 0) {
        counter = counter + 1
      }
      counter
    })
  }

  def group(lines: Lines, max: Int): PageMap = {
    var counter = 0
    var lineCounter = 0
    lines.groupBy[Int]((line: String) => {
      lineCounter = lineCounter + 1
      if (lineCounter > max) {
        counter = counter + 1
        lineCounter = 0
      }
      counter
    })
  }


  def read(readStream: ReadStream): Future[Lines] = {
    val interface: Interface = readline.createInterface(readStream)
    var seq: Array[String] = Array()
    val promise = concurrent.Promise[Lines]()
    interface.on("line", (s) => { seq = (seq :+ s.toString) })
    interface.on("close", (e) => {
      promise.success(seq)
      ()
    })
    promise.future
  }

  def pageToFile(section: Section,
                 page: Page,
                 aDir: String,
                 pageNumber: Int,
                 ctx: Context)(
                  implicit executionContext: ExecutionContext): Future[Section] = {
    val promise = concurrent.Promise[Section]()
    val file: String =
      s"${aDir}/${section.metaData.name}_${section.index}_${pageNumber}.md"
    fs.writeFile(
      file,
      page.mkString("\n"),
      (e) => {
        promise.success(
          section.copy(
            pagesOpt = section.pagesOpt.map(
              (pages: Array[PageSnippet]) =>
                pages :+ PageSnippet(
                  fileOpt = Some(file),
                  hashOpt = None,
                  htmlOpt = None
                ))))
        ()
      }
    )
    promise.future
  }

  def pagesToFiles(section: Section,
                   pages: PageMap,
                   aDir: String,
                   ctx: Context)(
                    implicit executionContext: ExecutionContext): Future[Section] = {

    pages.foldLeft(Future {
      section
    }: Future[Section])((aSectionFuture: Future[Section], t: (Int, Lines)) => {
      val counter: Int = t._1
      val lines: Page = t._2
      aSectionFuture.flatMap(aSection =>
        pageToFile(aSection, lines, aDir, counter, ctx))
    })

  }

  def toFiles(readStream: ReadStream, actx: Context)(
    implicit executionContext: ExecutionContext) = {
    val promise = concurrent.Promise[Future[Iterable[Section]]]()
    toSections(readStream, actx).map(aMap => {
      val aaDir = s"${actx.outPath}/${actx.metaData.name}"
      mkdirp(
        aaDir,
        (e: ErrnoException, m: Made) => {

          val fa: immutable.Iterable[Future[Section]] = aMap.map(t => {
            val f = pagesToFiles(section = t._1,
              group(t._2, 30),
              aDir = aaDir,
              ctx = actx)
            f
          })
          val ff = Future.sequence(fa)
          promise.success(ff)
        }
      )
    })
    promise.future.flatten
  }

  def toSections(
                  readStream: ReadStream,
                  ctx: Context
                )(implicit executionContext: ExecutionContext): Future[LinesMap] = {

    val p = concurrent.Promise[LinesMap]()

    read(readStream).map(lines => {
      var counter = 0;
      var aTitleOpt: Option[String] = None;
      var headerLevel = -1
      p.success(
        lines
          .groupBy[Section]((line: String) => {
            val aLevel = ctx.headerLevel(line)
            if (aLevel > 0) {
              headerLevel = aLevel
              counter = counter + 1
              aTitleOpt = Some(
                line.replaceAll("#", "").trim
              )
            }
            Section(level = headerLevel,
              index = counter,
              metaData = ctx.metaData,
              pagesOpt = None,
              titleOpt = aTitleOpt)
          }))
      //.map(t => (t._1, group(t._2, 30))))
    })
    p.future
  }

}
