package com.lyrx.text.processing

import com.lyrx.text.processing.Types.{Lines, Page, PageMap}
import com.lyrx.text.processing.filter.LinesFromFile
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node
import node.NodeJS.ErrnoException
import typings.node.childUnderscoreProcessMod.spawn
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
    htmlFrags: Iterable[String] = Seq(),
    sectionNum: Int = 0
)

object Taker {
  def apply() = new Taker(Taking())
}

class Taker(override val taking: Taking)
    extends LinesFromFile
    with Grouping2
    with Writer
    with Generator {

  def mdPath(s: String) =
    new Taker(taking.copy(mdInputPathOpt = Some(s)))

  def readMD()(implicit executionContext: ExecutionContext) =
    taking.mdInputPathOpt
      .map(path =>
        fromFile(path).map(lines =>
          new Taker(taking.copy(linesOpt = Some(lines)))))
      .getOrElse(Future { Taker.this })

  def id(s: String) =
    new Taker(taking.copy(idOpt = Some(s)))

  def slize(num: Int) =
    new Taker(taking.copy(slizeOpt = Some(num)))

  def grouping() =
    taking.linesOpt
      .flatMap(
        lines =>
          taking.slizeOpt.map(
            slize => new Taker(taking.copy(mapOpt = Some(group(lines, slize))))
        ))
      .getOrElse(Taker.this)

  def writeHTMLs()(implicit executionContext: ExecutionContext) =
    taking.idOpt
      .map(id =>
        mmkdirp(htmlFragPath(id)).flatMap(dir =>
          Future
            .sequence(taking.mdFrags.map(mdFrag => {
              val baseName =
                mdFrag.stripSuffix(".md").substring(mdFrag.lastIndexOf('/') + 1)
              pandoc(mdFrag, s"${dir}/${baseName}-frag.html")
            }))
            .map(it => new Taker(taking.copy(htmlFrags = it)))))
      .getOrElse(Future { Taker.this })

  def markdownFragPath(aId: String) = s"${taking.outPath}/markdown/${aId}"
  def htmlFragPath(aId: String) = s"${taking.outPath}/html/${aId}"

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
      .getOrElse(Future { Taker.this })

}
