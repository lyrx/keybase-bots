package com.lyrx.text.processing

import com.lyrx.text.processing.books._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
@JSExportTopLevel("Chunker")
object Main extends Koblach {

  @JSExport
  def initt() = {

    //karlMayBooks.map(chunk(_))
    //traktatus
    //doDickens()
    //generate(s"${books}/FriedrichSchiller")("aesthetik")
    generate()

    //doDiary()
    //doHegel()
    //haselis()

  }

}
