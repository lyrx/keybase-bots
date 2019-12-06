package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.{ExecutionContext, Future}

trait Koblach extends BooksBase {

  val kuendigung: String


  val koblach = s"${kuendigung}/koblach"

  def doKoblach() = {
    Taker()
      .id("koblach")
      .collectMarkdownMarks(s"${koblach}/novel.md", "t")
      .flatMap(_.collectMarkdownMarks(s"${kind}/Kriegskindeskind.groovy", "gr"))
      .flatMap(_.writeToPath(s"${koblach}/whois.md"))
      .flatMap(_.mdAndHTML())

  }

}
