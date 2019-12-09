package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.Future

trait CollectKoblach extends BooksBase  {
  val taker:Taker



  def collect()(implicit aroot:String,withPrefix:Boolean):Future[Taker] = taker
    .collectMarkdownMarks(
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
