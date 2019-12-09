package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.Future

trait Koblach extends BooksBase with CollectKoblach {

  implicit val aroot: String = s"${kuendigung}/koblach"
  implicit val withPrefix:Boolean=true

  override val taker: Taker = Taker()
    .id("koblach")

  def doKoblach() =
    collect()
      .flatMap(_.
        beautifyLines().
        writeToPath(s"${aroot}/generated.md"))
      .flatMap(_.mdAndHTML())

}
