package com.lyrx.text.processing

import com.lyrx.text.processing.Types.Lines
import com.lyrx.text.processing.filter.{Filters, LineFilter, LinesFromFile}

import scala.concurrent.ExecutionContext

trait CollectorFilter extends LinesFromFile {
  val taking: Taking

  def withFilter(f: Lines => Lines) =
    new Taker(taking.copy(filterOpt = Some(f)))

  def collectFrom(s: String)(implicit executionContext: ExecutionContext) =
    fromFile(s).map(lines =>
      new Taker(taking.copy(linesCollectorOpt = Some(lines))))

  def title(title:String)=
    takeMarkdown(s"# ${title} #")

  def img(src:String)=
    takeMarkdown(s"![](${src})")


  def takeMarkdown(line:String)=
    new Taker(taking.copy(linesOpt =
      Some(
        taking.linesOpt.getOrElse(Seq())
          :+ line
      )))



  def fromMark(mark: String): Taker =
    fromFilter(
          Filters.filterMarker(mark) _)

  def all(): Taker =
    fromFilter(Filters.ALL)

  def fromFilter(f: Lines=>Lines): Taker =
    new Taker(
      taking.copy(
        filterOpt = Some(f))).
      applyCollectorFilter()




  def applyCollectorFilter() =
    taking.filterOpt
      .flatMap(
        filter =>
          taking.linesCollectorOpt.map(
            collectorLines =>
              new Taker(taking.copy(linesOpt = Some(
                taking.linesOpt.getOrElse(Seq()) ++ filter(collectorLines))))
        ))
      .getOrElse(new Taker(taking))

  def filter(): Taker =
    taking.linesOpt
      .flatMap(lines =>
        taking.filterOpt.map(filter =>
          new Taker(taking.copy(linesOpt = Some(filter(lines))))))
      .getOrElse(new Taker(taking))

}
