package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.Future

trait Ideen extends BooksBase {

  override def cid() = "ideen"
  implicit override val aroot: String = s"${kuendigung}/${cid()}"
  override implicit val withPrefix: Boolean = false

  override def collect()(implicit aroot: String,
                         withPrefix: Boolean): Future[Taker] =
    taker
      .coll(s"${aroot}/ideen.md","sss")



}
