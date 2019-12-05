package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.ExecutionContext

trait Kriegskindeskind extends  BooksBase {

  val kuendigung:String


  val kind = s"${creative}/derjunge"



   def krieg() = {
    Taker()
      .id("kriegskindeskind")
      .collect(s"${kind}/Kriegskindeskind.md",Seq(
        "d1"
      )).
      flatMap(_.writeToPath(s"${kind}/generated.md")).
      flatMap(_.mdAndHTML())

  }


}
