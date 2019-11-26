package com.lyrx.text.processing.filter

import com.lyrx.text.processing.Types.Lines
import com.lyrx.text.processing.filter.Types.FilterFunc

import scala.concurrent.{ExecutionContext, Future}

trait CombinedLineFilter extends MarkerFilter with LineNumberFilter {
  val filters: Seq[FilterFunc]

  def filterLines(in: Seq[String]): Seq[String] =
    filters.foldLeft(in: Seq[String])((r, t) => t(r))

  def :+(filter: FilterFunc): CombinedLineFilter =
    new CombinedLineFilterImpl(filters :+ filter)

  def :+(from: Int, to: Int): CombinedLineFilter =
    new CombinedLineFilterImpl(filters :+ filterLineNumbers(from, to) _)
  def :+(marker: String): CombinedLineFilter =
    new CombinedLineFilterImpl(filters :+ filterMarks(marker) _)

}

object CombinedLineFilterImpl extends LinesFromFile {

  def apply() = new CombinedLineFilterImpl(Seq())

  def filterFile(file: String, filter: FilterFunc)(
      implicit
      executionContext: ExecutionContext) = fromFile(file).map(filter(_))

}

class CombinedLineFilterImpl(override val filters: Seq[FilterFunc])
    extends CombinedLineFilter {}
