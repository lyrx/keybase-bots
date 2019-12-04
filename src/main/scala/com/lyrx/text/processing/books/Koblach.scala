package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.{ExecutionContext, Future}

trait Koblach {

  val kuendigung:String
  implicit val exc:ExecutionContext

  val koblach = s"${kuendigung}/koblach"



   def doKoblach() = {
    Taker()
      .id("koblach")
      .collect(s"${koblach}/novel.md",Seq(
        "t1","t2"
      )).
      flatMap(_.writeToPath(s"${koblach}/whois.md")).
      flatMap(_.mdAndHTML())

  }


}
