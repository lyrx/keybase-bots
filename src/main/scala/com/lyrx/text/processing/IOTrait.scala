package com.lyrx.text.processing

import com.lyrx.text.processing.Types.{Lines, Page, PageMap}
import com.lyrx.text.processing.filter.LinesFromFile

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node
import node.childUnderscoreProcessMod.spawn
import node.{fsMod => fs, readlineMod => readline}
import node.NodeJS.ErrnoException

trait IOTrait extends LinesFromFile{

  val taking: Taking


  def writeLines(file:String)(
    implicit executionContext: ExecutionContext): Future[Taker] =
    writeAFile(file).
      map(_.map(s=>new Taker(taking))).
      getOrElse(Future{new Taker(taking)})



  def writeAFile(file:String)=taking.linesOpt.map(
    lines=> {
    val promise = Promise[String]()
    fs.writeFile(
      file,
      lines.mkString("\n"),
      (e) => {
        promise.success(file)
        ()
      })
      promise.future
  })

  def mmkdirp(dir: String) = {
    val promise = Promise[String]()
    mkdirp(dir,
           (e: ErrnoException, m: Made) =>
             if (e != null)
               promise.failure(e.asInstanceOf[Throwable])
             else
               promise.success(dir))
    promise.future
  }

  def ppageToFile(pageNumber: Int, sectionNumber:Int, page: Page, aDir: String)(
      implicit executionContext: ExecutionContext) =
    taking.idOpt
      .map(id => {
        val promise = concurrent.Promise[Option[String]]()
        val newName = s"${id}_${sectionNumber}_${pageNumber}"
        val file: String =
          s"${aDir}/${newName}.md"
        fs.writeFile(
          file,
          page.mkString("\n"),
          (e) => {
            promise.success(Some(file))
            ()
          }
        )
        promise.future
      })
      .getOrElse(Future { None })

  def ppagesToFiles(aDir: String)(implicit executionContext: ExecutionContext) =
    taking.mapOpt
      .map(amap =>
        Future.sequence(amap.map(page => {
          val index: Int = page._1
          val lines: Page = page._2
          ppageToFile(index, taking.sectionNum,lines, aDir)
        })))
      .getOrElse(Future { Seq() })
    .map(_.flatten)

  //pandoc /Users/alex/output/satanundischarioti/satanundischarioti_1_0.md -o /Users/alex/output/satanundischarioti/satanundischarioti_1_0-frag.html
  def toHTML(file: String,
             index:Int,
             sectionNum:Int,
             outPath: String)(implicit ctx: ExecutionContext) =
    taking.idOpt.map(id => {
      val promise = concurrent.Promise[IOTrait]
      val html = s"${outPath}/${id}_${index}-frag.html"
      spawn("pandoc", js.Array(file, "-o", html)).on(
        "close",
        (code) => {
          promise.success(IOTrait.this)
          ()
        }
      )
      promise.future
    })

}
