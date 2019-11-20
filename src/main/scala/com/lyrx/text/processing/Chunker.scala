package com.lyrx.text.processing

import Types._
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node
import node.NodeJS.ErrnoException
import node.fsMod.ReadStream
import node.readlineMod.Interface
import node.{fsMod => fs, readlineMod => readline}
import typings.node.childUnderscoreProcessMod.spawn

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
@JSExportTopLevel("Chunker")
object Main extends Chunker {



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
    implicit val exc = ExecutionContext.global
    val ctx = Context(
      headerLevel = h,
      metaData = MetaData(name = "satanundischarioti"),
      outPath = output
    )

    /*
toHTML(PageSnippet(
  Some(
    "/Users/alex/output/satanundischarioti/satanundischarioti_1_1.md"),
  None,
  None)).map(p=>println(p))


     */

    toFiles(readStream=fs.createReadStream(
      s"${may}/satanundischarioti.md"),
      actx = ctx).
      map(
      sections => sections.foreach(section => println(section))
    )

  }

}

case class PageSnippet(
    fileOpt: Option[String],
    hashOpt: Option[String],
    htmlOpt: Option[String]
)




case class Section(level: Int,
                   index: Int,
                   metaData: MetaData,
                   pagesOpt: Option[Array[PageSnippet]],
                   titleOpt: Option[String])

case class Context(
    headerLevel: HeaderDetection,
    metaData: MetaData,
    outPath: String
)

case class MetaData(name: String)

trait Chunker {



  //def sectionsToHTML(readStream: ReadStream)(implicit ctx:ExecutionContext)=toFiles(readStream)

  //pandoc /Users/alex/output/satanundischarioti/satanundischarioti_1_0.md -o /Users/alex/output/satanundischarioti/satanundischarioti_1_0-frag.html
  def toHTML(pageSnippet: PageSnippet)(implicit ctx: ExecutionContext) =
    pageSnippet.fileOpt
      .map(file => {
        val promise = concurrent.Promise[PageSnippet]
        val base = file.stripSuffix(".md")
        val html = s"${base}-frag.html"
        spawn("pandoc", js.Array(file, "-o", html)).on(
          "close",
          (code) => {
            promise.success(pageSnippet.copy(htmlOpt = Some(html)))
            ()
          }
        )
        promise.future
      })
      .getOrElse(Future { pageSnippet })

  def toHTMLs(a: Array[PageSnippet])(implicit ctx: ExecutionContext) =
    a.foldLeft(Future { Array() }: Future[Array[PageSnippet]])((f, s) =>
      f.flatMap(snippets => toHTML(s).map(p => snippets :+ p)))

  def sectionToHTML(section: Section)(implicit ctx: ExecutionContext) =
    section.pagesOpt
      .map(a => toHTMLs(a).map(pages => section.copy(pagesOpt = Some(pages))))
      .getOrElse(Future { section })

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

  /*
  def withPages(linesMap: LinesMap,max:Int):SectionMap = linesMap.
    map(t=>(t._1,group(t._2,max)))
   */

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
      implicit executionContext: ExecutionContext): Future[Array[Section]] = {
    val promise = concurrent.Promise[Future[Array[Section]]]()
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
          val ff = Future.sequence(fa).map(_.toArray)
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
