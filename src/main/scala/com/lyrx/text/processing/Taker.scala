package com.lyrx.text.processing

import com.lyrx.text.processing.Types.{Lines, PageMap}
import com.lyrx.text.processing.filter.{Filters, LineFilter, LinesFromFile}

import typings.node
import node.NodeJS.ErrnoException

import scala.concurrent.{ExecutionContext, Future}
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
    sectionNum: Int = 0,
    filterOpt: Option[Lines => Lines] = None,
    linesCollectorOpt: Option[Lines] = None
)

object Taker {
  def apply() = new Taker(Taking())
}

class Taker(override val taking: Taking)
    extends LinesFromFile
    with Grouping2
    with HTML2
    with IOTrait
    with CollectorFilter
    with Markdown {



  def id(s: String) =
    new Taker(taking.copy(idOpt = Some(s)))



  def mdAndHTML()(implicit executionContext: ExecutionContext) =
    toSections()
      .flatMap(sequence =>
        Future.sequence(sequence.map(taker =>
          taker.slize(30).grouping().writeMarkdowns().map(_.writeHTMLs()))))
      .map(Future.sequence(_))
      .flatten

}
