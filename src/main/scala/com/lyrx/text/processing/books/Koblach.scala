package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.ExecutionContext

trait Koblach {

  val kuendigung:String
  implicit val exc:ExecutionContext

  val koblach = s"${kuendigung}/koblach"
   def doKoblach() = {
    Taker()
      .id("koblach")
      .collectFrom(s"${koblach}/novel.md")
      .flatMap(_.fromMark("t1").
        writeToPath(s"${koblach}/whois.md").
        flatMap(t=>t.mdAndHTML())
      )

  }


}
