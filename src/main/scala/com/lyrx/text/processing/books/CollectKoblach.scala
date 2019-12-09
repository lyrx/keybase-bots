package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

trait CollectKoblach extends BooksBase  {
  val taker:Taker
  val novel:String


  def collect()(implicit aroot:String) = taker
    .collectMarkdownMarks(s"${novel}", "t")
    .col(s"${kind}/Kriegskindeskind.groovy", "gr")
    .col(s"novel.md", "sss")
    .col(s"attic2/aufwachen.md", "sss")

}
