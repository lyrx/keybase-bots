package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.ExecutionContext

trait Kriegskindeskind extends BooksBase {

  val kuendigung: String


  implicit val withPrefixx:Boolean=true

  def krieg() =
    Taker()
      .id("kriegskindeskind")
      .coll(
        s"${kind}/Kriegskindeskind.md",
        "krd")

      .flatMap(_.writeToPath(s"${kind}/generated.md"))
      .flatMap(_.mdAndHTML())

}
