package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

trait CollectKoblach extends BooksBase  {
  val taker:Taker



  def collect()(implicit aroot:String,withPrefix:Boolean) = taker
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

}
