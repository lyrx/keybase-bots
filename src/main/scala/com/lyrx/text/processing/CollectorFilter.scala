package com.lyrx.text.processing

import com.lyrx.text.processing.Types.Lines
import com.lyrx.text.processing.filter.{Filters, LinesFromFile}

import scala.concurrent.{ExecutionContext, Future}

trait CollectorFilter extends LinesFromFile{
  val taking: Taking


  def withFilter(f: Lines => Lines) =
    new Taker(taking.copy(filterOpt = Some(f)))


  def collectFrom(s: String)(implicit executionContext: ExecutionContext) =
    fromFile(s).map(lines =>
      new Taker(taking.copy(linesCollectorOpt = Some(lines))))

  def fromMark(mark: String): Taker =
    new Taker(
      taking.copy(
        filterOpt = Some(
          Filters.filterMarker(mark) _
        ))).applyCollectorFilter()

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



  def filter():Taker = taking.linesOpt.
    flatMap(lines=>taking.
      filterOpt.
      map(filter=>
        new Taker(taking.copy(
          linesOpt=Some(
            filter(lines)))))).
    getOrElse(
      new Taker(taking))





}
