package com.lyrx.text.processing.filter

trait LineNumberFilter  {



    def filterLineNumbers(p:(Int,Int))(in: Seq[String]):Seq[String] =in.zipWithIndex
      .dropWhile( _._2 < p._1)
      .drop(p._2 - p._2)
      .map(_._1)



}
