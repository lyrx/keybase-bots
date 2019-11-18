package com.lyrx.text.processing

import scala.scalajs.js
import typings.node
import node.fsMod.ReadStream
import node.readlineMod.Interface
import node.{fsMod => fs, readlineMod => readline}
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node.NodeJS.ErrnoException

import js.annotation.{JSExport, JSExportTopLevel}
import scala.collection.immutable
import scala.concurrent
import scala.concurrent.{ExecutionContext, Future}
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
    toFiles(fs.createReadStream(s"${may}/satanundischarioti.md"))(
      Context(
        headerLevel = h,
        metaData = MetaData(
          name = "satanundischarioti"),
        outPath = output,
        executionContext = ExecutionContext.global
      )).map(
      sections=>sections.foreach(section=>println(section))
    )(ExecutionContext.global)
  }

}

case class Section(level: Int,
                    index:Int,
                   metaData: MetaData,
                   fileOpt:Option[String],
                   titleOpt:Option[String]
                  )

case class Context(
    headerLevel: Main.HeaderDetection,
    metaData: MetaData,
    outPath: String,
    executionContext: ExecutionContext
)

case class MetaData(name: String)

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

  def toFile(section: Section,
             array: Array[String],
             aDir:String
            )(implicit ctx: Context) = {
    val promise =concurrent.Promise[Section]()
    val file:String = s"${aDir}/${section.metaData.name}_${section.index}.md"
    fs.writeFile(file,
      array.mkString("\n"),
      (e)=>{
        promise.success(section.copy(fileOpt=Some(file)))
        ()
      })
    promise.future
  }

  def toFiles(readStream: ReadStream)(implicit ctx: Context) = {
    val promise =concurrent.Promise[Future[Array[Section]]]()
    toSections(readStream).map( aMap=> {
      val aDir = s"${ctx.outPath}/${ctx.metaData.name}"
      mkdirp (aDir,(e: ErrnoException, m: Made) => {
        implicit val executionContext = ctx.executionContext
        val fa = aMap.map(t=>{
          val f = toFile(t._1,t._2,aDir)
          f
        })
        val ff = Future.
          sequence(fa).
          map(_.toArray)
        promise.success(ff)
      })
    })(ctx.executionContext)
    promise.future.flatten
  }

  def toSections(
      readStream: ReadStream
  )(implicit ctx: Context) = {

    val p = concurrent.Promise[Map[Section, Array[String]]]()

    read(readStream).map( lines => {
      var counter = 0;
      p.success(lines.groupBy[Section]((line: String) => {
        val aLevel = ctx.headerLevel(line)
        var aTitleOpt:Option[String]=None;
        if ( aLevel > 0) {
          counter = counter + 1
          aTitleOpt=Some(
            line.
              replaceAll("#","").
              trim
          )
        }
        Section(level = aLevel,
          index = counter,
          metaData=ctx.metaData,
          fileOpt = None,
          titleOpt = aTitleOpt
        )
      }))
    })(ctx.executionContext)
    p.future
  }

}
