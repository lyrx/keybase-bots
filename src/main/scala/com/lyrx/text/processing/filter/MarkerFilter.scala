package com.lyrx.text.processing.filter

trait MarkerFilter  {

     def filterMarks(marker:String)(in: Seq[String]):Seq[String] =
        in.dropWhile(!_.matches(marker)).
          drop(1).
          takeWhile(!_.matches(marker))

}
