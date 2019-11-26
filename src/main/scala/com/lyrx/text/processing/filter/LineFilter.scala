package com.lyrx.text.processing.filter

trait LineFilter[T] {

  def filterLines(params:T)(in: Seq[String]): Seq[String]

}
