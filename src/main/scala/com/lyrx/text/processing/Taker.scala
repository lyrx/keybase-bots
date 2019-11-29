package com.lyrx.text.processing

import com.lyrx.text.processing.Types.{Lines, Page, PageMap}
import com.lyrx.text.processing.filter.LinesFromFile
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node
import node.{fsMod => fs, readlineMod => readline}
import node.NodeJS.ErrnoException
import typings.node.fsMod.PathLike

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.|
case class Taking(
    mdInputPathOpt: Option[String] = None,
    idOpt: Option[String] = None,
    slizeOpt: Option[Int] = None,
    linesOpt: Option[Lines] = None,
    mapOpt: Option[PageMap] = None,
    outPath: String = "/Users/alex/output",
    mdOutputPathOpt: Option[String] = None,
    mdFrags: Iterable[String] = Seq(),
    sectionNum: Int = 0
)

object Taker {
  def apply() = new Taker(Taking())
}

class Taker(override val taking: Taking)
    extends LinesFromFile
    with Grouping2
    with Writer {

  def mdPath(s: String) =
    new Taker(taking.copy(mdInputPathOpt = Some(s)))

  def id(s: String) =
    new Taker(taking.copy(idOpt = Some(s)))

  def slize(num: Int) =
    new Taker(taking.copy(slizeOpt = Some(num)))

  def readMD()(implicit executionContext: ExecutionContext) =
    taking.mdInputPathOpt
      .map(path =>
        fromFile(path).map(lines =>
          new Taker(taking.copy(linesOpt = Some(lines)))))
      .getOrElse(Future { Taker.this })

  def grouping() =
    taking.linesOpt
      .flatMap(
        lines =>
          taking.slizeOpt.map(
            slize => new Taker(taking.copy(mapOpt = Some(group(lines, slize))))
        ))
      .getOrElse(Taker.this)

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

  def writeHTMLs()(implicit executionContext: ExecutionContext) =
    taking.idOpt.map(id =>
      mmkdirp(s"${taking.outPath}/html/${id}").map(dir =>
        listMarkdownFrags().map(frags => {})))

  def writeMarkdowns()(implicit executionContext: ExecutionContext) =
    taking.idOpt
      .map(
        id => {
          val promise = concurrent.Promise[Taker]()
          val markdownOutputDir =
            s"${taking.outPath}/markdown/${id}"
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
      .getOrElse(Future { Taker.this })

}
