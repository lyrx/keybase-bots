package com.lyrx.text.processing.books

import com.lyrx.text.processing.Main.books
import com.lyrx.text.processing.Taker
import com.lyrx.text.processing.filter.Filters

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

trait Philosophie {

  implicit val exc:ExecutionContext
  def books:String
  def generate(base: String)(id: String): Future[immutable.Iterable[Taker]]

  private def doHegel(){
    generate(
      s"${books}/GeorgWilhelmFriedrichHegel")(
      "phnomenologiedesgeistes")

  }

  private def traktatus = {
    Taker()
      .mdPath(s"${books}/LudwigWittgenstein/tractatus.md")
      .id("traktatus")
      .withFilter(Filters.tractatus)
      .readMD()
      .flatMap(_.filter().mdAndHTML())
  }


}
