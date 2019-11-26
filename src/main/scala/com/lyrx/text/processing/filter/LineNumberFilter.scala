package com.lyrx.text.processing.filter

trait LineNumberFilter extends LineFilter {

   val from:Int
  val to:Int

    override def filterLines(in: Seq[String]):Seq[String] =in.zipWithIndex
      .dropWhile( _._2 < from)
      .drop(to-from)
      .map(_._1)



}
