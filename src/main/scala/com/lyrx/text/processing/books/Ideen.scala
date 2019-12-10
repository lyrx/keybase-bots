package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.Future

trait Ideen extends BooksBase  {

  override def cid()="ideen"

  override implicit val withPrefix:Boolean=false





  override def collect()(implicit aroot:String,withPrefix:Boolean):Future[Taker] = taker
    .collectMarkdownFrom(
      s"${aroot}/ideen.md")
    .chapter(
      "China")



}
