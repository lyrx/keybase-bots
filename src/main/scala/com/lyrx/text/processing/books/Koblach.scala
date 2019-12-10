package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.Future

trait Koblach extends BooksBase  {

  override implicit val aroot: String = s"${kuendigung}/koblach"
  override implicit val withPrefix:Boolean=true
  override val taker: Taker = Taker().id("koblach")


  def doKoblach() = collect().finish()

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
