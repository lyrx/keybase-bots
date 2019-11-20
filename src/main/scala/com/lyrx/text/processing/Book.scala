package com.lyrx.text.processing

import Types._

import scala.concurrent.{ExecutionContext, Future}

class Book(
            sections:  Array[Section]
          ) extends Chunker {


  def toHTML()(implicit ctx: ExecutionContext) =
    sections.foldLeft(Future {
      Array()
    }: Future[Array[Section]])((f, s) =>
      f.flatMap(ss => sectionToHTML(s).map(sss => ss :+ sss)))
}
