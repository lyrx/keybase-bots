package com.lyrx.text.processing

import com.lyrx.text.processing.filter.LinesFromFile

import scala.concurrent.{ExecutionContext, Future}

trait Collector extends LinesFromFile{
  val taking: Taking

  def filter():Taker = taking.linesOpt.
    flatMap(lines=>taking.
      filterOpt.
      map(filter=>
        new Taker(taking.copy(
          linesOpt=Some(
            filter(lines)))))).
    getOrElse(
      new Taker(taking))





}
