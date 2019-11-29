package com.lyrx.text.processing

import com.lyrx.text.processing.Types.{Lines, Page, PageMap}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node
import node.childUnderscoreProcessMod.spawn
import node.{fsMod => fs, readlineMod => readline}
import node.NodeJS.ErrnoException

trait Writer {

  val taking: Taking

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
      val promise = concurrent.Promise[Writer]
      val html = s"${outPath}/${id}_${index}-frag.html"
      spawn("pandoc", js.Array(file, "-o", html)).on(
        "close",
        (code) => {
          promise.success(Writer.this)
          ()
        }
      )
      promise.future
    })

}
