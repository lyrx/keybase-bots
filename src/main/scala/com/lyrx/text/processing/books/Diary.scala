package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.ExecutionContext

trait Diary {

  val kuendigung:String
  implicit val exc:ExecutionContext

  val diary:String = s"${kuendigung}/diary"

  private def doDiary() =
    Taker()
      .id("diary")
      .collectFrom(s"${diary}/ttagebuch.md")
      .flatMap(
        _.title("Sonntag, erster Dezember 2019")
          .img(
            "images/IMG_1948.jpeg",
            "Weihnachtsbaum des Bonifatius-Heims"
          )
          .fromMark("d1")
          .writeToPath(s"${diary}/mydiary.md")
          .flatMap(t=>t.mdAndHTML())
      )

}
