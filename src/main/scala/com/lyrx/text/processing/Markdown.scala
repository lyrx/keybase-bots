package com.lyrx.text.processing

import typings.node
import node.NodeJS.ErrnoException
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.|
import node.{fsMod => fs}
import typings.mkdirp.mkdirpMod.Made
trait Markdown extends IOTrait {

  val taking: Taking


  def markdownFragPath(aId: String) = s"${taking.outPath}/markdown/${aId}"

  def writeMarkdowns()(implicit executionContext: ExecutionContext) =
    taking.idOpt
      .map(
        id => {
          val promise = concurrent.Promise[Taker]()
          val markdownOutputDir = markdownFragPath(id)
          mkdirp(
            markdownOutputDir,
            (e: ErrnoException, m: Made) =>
              if (e != null)
                promise.failure(e.asInstanceOf[Throwable])
              else
                ppagesToFiles(markdownOutputDir).map(
                  (r: Iterable[String]) =>
                    promise.success(
                      new Taker(
                        this.taking.copy(
                          mdOutputPathOpt = Some(markdownOutputDir),
                          mdFrags = r
                        ))
                    ))
          )
          promise.future
        }
      )
      .getOrElse(Future { new Taker(taking) })


  def mdPath(s: String) =
    new Taker(taking.copy(mdInputPathOpt = Some(s)))

  def readMD()(implicit executionContext: ExecutionContext) =
    taking.mdInputPathOpt
      .map(path =>
        fromFile(path).map(lines =>
          new Taker(taking.copy(linesOpt = Some(lines)))))
      .getOrElse(Future { new Taker(taking) })




  def listMarkdownFrags()(implicit executionContext: ExecutionContext) = {
    val promise = Promise[js.Array[String]]()
    taking.mdOutputPathOpt.map(
      path =>
        fs.readdir(path, (e: ErrnoException | Null, r: js.Array[String]) => {
          ()
        })
    )
    promise.future
  }

}
