package com.lyrx.text.processing

import com.lyrx.text.processing.Types._

import scala.concurrent.ExecutionContext

trait Grouping2 {

  val taking: Taking

  def toPars(lines: Lines): ParMap = {
    var counter = 0
    lines.groupBy[Int]((line: String) => {
      if (line.trim.length == 0) {
        counter = counter + 1
      }
      counter
    })
  }

  def grouping() =
    taking.linesOpt
      .flatMap(
        lines =>
          taking.slizeOpt.map(
            slize => new Taker(taking.copy(mapOpt = Some(group(lines, slize))))
          ))
      .getOrElse(new Taker(taking))

  def group(lines: Lines, max: Int): PageMap = {
    var counter = 0
    var lineCounter = 0
    lines.groupBy[Int]((line: String) => {
      lineCounter = lineCounter + 1
      if (lineCounter > max) {
        counter = counter + 1
        lineCounter = 0
      }
      counter
    })
  }

  def detectHeader(line: String) = {
    val s = line.trim

    if (s.startsWith("####"))
      4
    else if (s.startsWith("###"))
      3
    else if (s.startsWith("##"))
      2
    else if (s.startsWith("#"))
      1
    else
      0
  }

  def toSections()(implicit executionContext: ExecutionContext) =
    toSectionsMap().map(
      _.map(t => {
        new Taker(
          taking.copy(
            sectionNum = t._1,
            linesOpt = Some(t._2)
          ))
      })
    )

  def toSectionsMap()(implicit executionContext: ExecutionContext) = {

    val p = concurrent.Promise[Map[Int, Lines]]()

    taking.linesOpt.map(lines => {
      var counter = 0;
      var aTitleOpt: Option[String] = None;
      var headerLevel = -1

      val grouped = lines
        .groupBy[Int]((line: String) => {
          val aLevel = detectHeader(line)
          if (aLevel > 0) {
            headerLevel = aLevel
            counter = counter + 1
            counter
          } else counter
        })
      p.success(grouped)
    })
    p.future
  }

}
