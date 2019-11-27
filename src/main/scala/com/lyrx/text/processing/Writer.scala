package com.lyrx.text.processing

import com.lyrx.text.processing.Types.{Lines, Page, PageMap}

import scala.concurrent.{ExecutionContext, Future}
import typings.node
import node.{fsMod => fs, readlineMod => readline}

trait Writer {

  val taking:Taking


  def ppageToFile(pageNumber: Int,
                  page: Page,
                  aDir: String)(
                   implicit executionContext: ExecutionContext) =
    taking.idOpt.map(id=>{
      val promise = concurrent.Promise[Writer]()
      val newName = s"${id}_${pageNumber}"
      val file: String =
        s"${aDir}/${newName}.md"
      println(file)
      fs.writeFile(
        file,
        page.mkString("\n"),
        (e) => {
          promise.success(Writer.this)
          ()
        }
      )
      promise.future
    }).getOrElse(Future{Writer.this})

  def ppagesToFiles(aDir: String)(
                    implicit executionContext: ExecutionContext) =
    taking.mapOpt.map(amap=>
      Future.sequence(amap.map(page=>{
        val index:Int = page._1
        val lines:Page = page._2
        ppageToFile(index,lines,aDir)
      }))).
      getOrElse(Future{Seq()})






}
