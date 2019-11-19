package com.lyrx.text.processing


import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node
import node.NodeJS.ErrnoException
import node.fsMod.ReadStream
import node.readlineMod.Interface
import node.{fsMod => fs, readlineMod => readline}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
@JSExportTopLevel("Chunker")
object Main extends Chunker {


  type HeaderDetection = String => Int
  type Par = Array[String]
  type Pars = Array[Par]
  type Lines = Par
  type SectionMap = Map[Section, LineMap]
  type ParMap = Map[Int, Par]
  type LineMap = Map[Int, Lines]


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
                   filesOpt:Option[Array[String]],
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

  import com.lyrx.text.processing.Main.{LineMap, Lines, Par, ParMap, SectionMap}

  def toPars(lines:Lines): ParMap ={
    var  counter = 0
    lines.
      groupBy[Int]((line:String)=>{
        if(line.trim.length == 0){
          counter = counter + 1
        }
        counter
      })
  }

  def group(lines:Lines, max:Int): LineMap ={
    var  counter = 0
    var lineCounter = 0
    lines.
      groupBy[Int]((line:String)=>{
        lineCounter = lineCounter + 1
        if(lineCounter > max){
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


  def toFile(section: Section,
             array: Lines,
             aDir:String,
             pageNumber:Int
            )(implicit ctx: Context): Future[Section] = {
    val promise =concurrent.Promise[Section]()
    val file:String = s"${aDir}/${section.metaData.name}_${section.index}_${pageNumber}.md"
    fs.writeFile(file,
      array.mkString("\n"),
      (e)=>{
        promise.success(section.copy(filesOpt=section.filesOpt.map(l=>l:+file)))
        ()
      })
    promise.future
  }



  def pagesToFiles(section: Section,
             lineMap: LineMap,
             aDir:String
            )(implicit ctx: Context): Future[Section] = {
    val promise =concurrent.Promise[Section]()


    implicit val executionContext = ctx.executionContext


   val r: Future[Section] =  lineMap.foldLeft(Future{
      section
    }:Future[Section]) (
     (aSectionFuture:Future[Section],t:(Int,Lines))=>{
      val counter:Int = t._1
      val lines:Lines = t._2
      aSectionFuture.flatMap(aSection=>
      toFile(aSection,lines,aDir,counter))
    })



    /*
    fs.writeFile(file,
      array.mkString("\n"),
      (e)=>{
        promise.success(section.copy(fileOpt=Some(file)))
        ()
      })


     */


    promise.future
  }

  def toFiles(readStream: ReadStream)(implicit ctx: Context): Future[Array[Section]] = {
    val promise =concurrent.Promise[Future[Array[Section]]]()
    toSections(readStream).map( aMap=> {
      val aDir = s"${ctx.outPath}/${ctx.metaData.name}"
      mkdirp (aDir,(e: ErrnoException, m: Made) => {
        implicit val executionContext = ctx.executionContext
        val fa: immutable.Iterable[Future[Section]] = aMap.map(t=>{
          val f = pagesToFiles(t._1,t._2,aDir)
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
  )(implicit ctx: Context): Future[SectionMap] = {

    val p = concurrent.Promise[SectionMap]()

    read(readStream).map( lines => {
      var counter = 0;
      var aTitleOpt:Option[String]=None;
      var headerLevel = -1
      p.success(lines.groupBy[Section]((line: String) => {
        val aLevel = ctx.headerLevel(line)
        if ( aLevel > 0) {
          headerLevel = aLevel
          counter = counter + 1
          aTitleOpt=Some(
            line.
              replaceAll("#","").
              trim
          )
        }
        Section(level = headerLevel,
          index = counter,
          metaData=ctx.metaData,
          filesOpt = None,
          titleOpt = aTitleOpt
        )
      }).
        map(t=>(t._1,group(t._2,30))))
    })(ctx.executionContext)
    p.future
  }

}
