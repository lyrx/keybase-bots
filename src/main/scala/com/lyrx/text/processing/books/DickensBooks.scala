package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker

import scala.concurrent.Future

trait DickensBooks {
  val books:String
  val dickens = s"${books}/CharlesDickens"

  def generate(base: String)(id: String):Future[Iterable[Taker]]


  def doDickens() = {
    val d = generate(dickens) _
    Seq("greatexpectations", "littledorrit", "olivertwist").map(d(_))

  }

}
