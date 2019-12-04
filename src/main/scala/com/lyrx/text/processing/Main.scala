package com.lyrx.text.processing

import com.lyrx.text.processing.Types.HeaderDetection
import com.lyrx.text.processing.books.{BooksBase, Diary, DickensBooks, Haesschen, KarlMayBooks, Koblach, Philosophie}
import com.lyrx.text.processing.filter.Filters
import typings.node
import node.{fsMod => fs}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
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
    with Diary {


  @JSExport
  def initt() = {

    // karlMayBooks.map(chunk(_))
    // traktatus
    // doDickens()
    // generate(s"${books}/FriedrichSchiller")("aesthetik")
     doKoblach
    // doDiary()
    //  doHegel()
    //haselis()

  }



}
