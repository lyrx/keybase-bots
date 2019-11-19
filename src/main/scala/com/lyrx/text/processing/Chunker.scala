package com.lyrx.text.processing

import com.lyrx.text.processing.Main.{LinesMap, Page}
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

  type HeaderDetection = String => Int
  type Par = Array[String]
  type Pars = Array[Par]
  type Lines =  Array[String]
  type SectionMap = Map[Section, PageMap]
  type LinesMap = Map[Section,Lines]
  type ParMap = Map[Int, Par]
  type PageMap = Map[Int, Lines]
  type Page = Array[String]



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
    toFiles(fs.createReadStream(s"${may}/satanundischarioti.md"))(
      Context(
        headerLevel = h,
        metaData = MetaData(name = "satanundischarioti"),
        outPath = output,
        executionContext = ExecutionContext.global
      )).map(
      sections => sections.foreach(section => println(section))
    )(ExecutionContext.global)
  }

}

case class PageSnippet(
                      fileOpt:Option[String],
                      hashOpt:Option[String]
               )

case class Section(level: Int,
                   index: Int,
                   metaData: MetaData,
                   pagesOpt: Option[Array[PageSnippet]],
                   titleOpt: Option[String])

case class Context(
    headerLevel: Main.HeaderDetection,
    metaData: MetaData,
    outPath: String,
    executionContext: ExecutionContext
)

case class MetaData(name: String)

trait Chunker {

  import com.lyrx.text.processing.Main.{PageMap, Lines, Par, ParMap, SectionMap}



  //pandoc /Users/alex/output/satanundischarioti/satanundischarioti_1_0.md -o /Users/alex/output/satanundischarioti/satanundischarioti_1_0-frag.html
  def toHTML(pageSnippet: PageSnippet): Unit ={
    spawn("pandoc",js.Array("",""))
  }

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

  def pageToFile(section: Section, page: Page, aDir: String, pageNumber: Int)(
      implicit ctx: Context): Future[Section] = {
    val promise = concurrent.Promise[Section]()
    val file: String =
      s"${aDir}/${section.metaData.name}_${section.index}_${pageNumber}.md"
    fs.writeFile(file, page.mkString("\n"), (e) => {
      promise.success(
        section.copy(
          pagesOpt = section.pagesOpt.map((pages:Array[PageSnippet])=>pages :+ PageSnippet(
          fileOpt = Some(file),
          hashOpt = None))))
      ()
    })
    promise.future
  }

  def pagesToFiles(section: Section, pages: PageMap, aDir: String)(
      implicit ctx: Context): Future[Section] = {
    implicit val executionContext = ctx.executionContext

    pages.foldLeft(Future {
      section
    }: Future[Section])(
      (aSectionFuture: Future[Section], t: (Int, Lines)) => {
      val counter: Int = t._1
      val lines: Page= t._2
      aSectionFuture.flatMap(aSection => pageToFile(aSection, lines, aDir, counter))
    })

  }

  def toFiles(readStream: ReadStream)(
      implicit ctx: Context): Future[Array[Section]] = {
    val promise = concurrent.Promise[Future[Array[Section]]]()
    toSections(readStream).map(aMap => {
      val aDir = s"${ctx.outPath}/${ctx.metaData.name}"
      mkdirp(
        aDir,
        (e: ErrnoException, m: Made) => {
          implicit val executionContext = ctx.executionContext
          val fa: immutable.Iterable[Future[Section]] = aMap.map(t => {
            val f = pagesToFiles(t._1, group(t._2,30), aDir)
            f
          })
          val ff = Future.sequence(fa).map(_.toArray)
          promise.success(ff)
        }
      )
    })(ctx.executionContext)
    promise.future.flatten
  }




  def toSections(
      readStream: ReadStream
  )(implicit ctx: Context): Future[LinesMap] = {


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
    })(ctx.executionContext)
    p.future
  }

}
