package com.lyrx.text.processing.filter

trait LineFilter {

  def filterLines(in: Seq[String]): Seq[String]

}
