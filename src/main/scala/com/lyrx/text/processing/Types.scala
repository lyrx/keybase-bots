package com.lyrx.text.processing

object Types {
  type HeaderDetection = String => Int
  type Par = Seq[String]
  type Pars = Seq[Par]
  type Lines = Seq[String]
  type SectionMap = Map[Section, PageMap]
  type LinesMap = Map[Section, Lines]
  type ParMap = Map[Int, Par]
  type PageMap = Map[Int, Lines]
  type Page = Seq[String]

}
