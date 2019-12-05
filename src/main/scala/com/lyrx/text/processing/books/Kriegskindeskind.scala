package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.ExecutionContext

trait Kriegskindeskind extends  BooksBase {

  val kuendigung:String


  val kind = s"${creative}/derjunge"



   def krieg() = {
    Taker()
      .id("kriegskindeskind")
      .collectMarks(s"${kind}/Kriegskindeskind.md","krd").
      flatMap(_.writeToPath(s"${kind}/generated.md")).
      flatMap(_.mdAndHTML())

  }


}
