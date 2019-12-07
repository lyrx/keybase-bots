package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.Future

trait Koblach extends BooksBase {

  val kuendigung: String
  val koblach = s"${kuendigung}/koblach"
  val novel = s"${koblach}/novel.md"

  implicit class FutureTaker(taker:Future[Taker]){

    def col(file:String,prefix:String)=
      taker.flatMap(_.
        collectMarkdownMarks(
          if(file.startsWith("/"))
            s"${file}"
            else
          s"${koblach}/${file}"
          ,
          prefix
        ))
  }

  def doKoblach() = {
    Taker()
      .id("koblach")
      .collectMarkdownMarks(s"${novel}", "t")


      .col(s"${kind}/Kriegskindeskind.groovy", "gr")
      .col(s"novel.md", "sss")
      .col(s"attic2/aufwachen.md", "sss")


      .flatMap(_.writeToPath(s"${koblach}/generated.md"))
      .flatMap(_.mdAndHTML())

  }

}
