package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.Future

trait Koblach extends BooksBase  {

  override def cid()="koblach"

  override implicit val withPrefix:Boolean=true





  override def collect()(implicit aroot:String,withPrefix:Boolean):Future[Taker] = taker
    .coll(
      s"${aroot}/novel.md",
      "t")
    .col(
      s"${kind}/Kriegskindeskind.groovy",
      "gr")
    .col(
      s"novel.md",
      "sss")
    .col(
      s"attic2/aufwachen.md",
      "sss")
    .col(
      s"attic2/ki2i.md",
      "kii")
    .col(
      "attic2/identity.md",
      "sss")



}
