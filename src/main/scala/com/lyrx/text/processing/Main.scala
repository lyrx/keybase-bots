package com.lyrx.text.processing

import com.lyrx.text.processing.books._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
@JSExportTopLevel("Chunker")
object Main
    extends Chunker
    with BooksBase
    with KarlMayBooks
    with DickensBooks
    with Haesschen
    with Philosophie
    with Koblach
    with Diary
    with Kriegskindeskind {

  @JSExport
  def initt() = {

    //karlMayBooks.map(chunk(_))
    //traktatus
    //doDickens()
    //generate(s"${books}/FriedrichSchiller")("aesthetik")
    doKoblach
    krieg()
    //doDiary()
    //doHegel()
    //haselis()

  }

}
