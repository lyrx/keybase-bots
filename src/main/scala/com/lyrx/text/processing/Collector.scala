package com.lyrx.text.processing

import scala.concurrent.{ExecutionContext, Future}

trait Collector {
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
