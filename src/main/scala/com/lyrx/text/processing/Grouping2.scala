package com.lyrx.text.processing

import com.lyrx.text.processing.Types._
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node
import typings.node.NodeJS.ErrnoException
import typings.node.fsMod.ReadStream
import typings.node.readlineMod.Interface
import typings.node.{fsMod => fs, readlineMod => readline}

import scala.concurrent.{ExecutionContext, Future}

trait Grouping2 {

  def toPars(lines: Lines): ParMap = {
    var counter = 0
    lines.groupBy[Int]((line: String) => {
      if (line.trim.length == 0) {
        counter = counter + 1
      }
      counter
    })
  }

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



}
