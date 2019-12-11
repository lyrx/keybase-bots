package com.lyrx.text.processing

import com.lyrx.text.processing.books._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
@JSExportTopLevel("Chunker")
object Main extends Koblach {
  val GATEWAY="ipfs.lyrx.de"
  def SNIPPETS()="/ipns/QmS9RqAEWd4fKNiDCRgDKnWT3mDB5q9VkXsZJFCcw5gya5"


  val generators:Seq[BooksBase] = Seq(
    new Koblach {},
    new Ideen{},
    new Pyramids {}
  )


  @JSExport
  def initt() = {

   generators.foreach(_.generate())

    //karlMayBooks.map(chunk(_))
    //traktatus
    //doDickens()
    //generate(s"${books}/FriedrichSchiller")("aesthetik")


    //doDiary()
    //doHegel()
    //haselis()

  }

}
