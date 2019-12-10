package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.Future

trait Pyramids extends BooksBase {

  override def cid() = "pyramids"

  override implicit val withPrefix: Boolean = false

  override def collect()(implicit aroot: String,
                         withPrefix: Boolean): Future[Taker] =
    taker
      .collectMarkdownFrom(
        s"${aroot}/usecase.md")
      .map(_.all())

}
