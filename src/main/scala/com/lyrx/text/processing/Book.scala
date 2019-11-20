package com.lyrx.text.processing

import Types._

import scala.concurrent.{ExecutionContext, Future}

class Book(
            sectionMap: SectionMap
          ) extends Chunker {
  def sections(): Array[Section] = sectionMap.keys.toArray

  def toHTML()(implicit ctx: ExecutionContext) =
    sections().foldLeft(Future {
      Array()
    }: Future[Array[Section]])((f, s) =>
      f.flatMap(ss => sectionToHTML(s).map(sss => ss :+ sss)))
}
