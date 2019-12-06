package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.{ExecutionContext, Future}

trait Koblach extends BooksBase {

  val kuendigung: String


  val koblach = s"${kuendigung}/koblach"
  val novel = s"${koblach}/novel.md"

  def doKoblach() = {
    Taker()
      .id("koblach")

      .collectMarkdownMarks(s"${novel}", "t")
      .flatMap(_.collectMarkdownMarks(s"${kind}/Kriegskindeskind.groovy", "gr"))
      .flatMap(_.collectMarkdownMarks(s"${novel}", "sss"))

      
      .flatMap(_.writeToPath(s"${koblach}/generated.md"))
      .flatMap(_.mdAndHTML())

  }

}
