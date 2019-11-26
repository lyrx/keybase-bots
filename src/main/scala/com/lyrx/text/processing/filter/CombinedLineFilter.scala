package com.lyrx.text.processing.filter



trait CombinedLineFilter {
  val filters:Seq[LineFilter]

  def filterLines(in: Seq[String]): Seq[String] = filters.
    foldLeft(in: Seq[String])((r,t)=>t.filterLines(r))


  def :+(filter:LineFilter): CombinedLineFilter = new
      CombinedLineFilterImpl(filters :+ filter)

  def :+(from:Int,to:Int): CombinedLineFilter = new
      CombinedLineFilterImpl(filters :+ new LineNumberFilterImpl(from,to))


}
