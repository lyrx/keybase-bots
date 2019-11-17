package com.lyrx.text.processing


import typings.node.{Buffer, process, fsMod => fs, readlineMod => readline}
import typings.node.NodeJS.{ErrnoException, WritableStream}
import typings.node.fsMod.ReadStream
import typings.node.readlineMod.Interface

import scala.scalajs.js
import js.annotation.{JSExport, JSExportTopLevel}
import js.|


@JSExportTopLevel("Chunker")
object Main extends Chunker {

  @JSExport
  def initt(): Unit = {
    read(fs.createReadStream("build.sbt"),_.foreach(println))
  }
}


trait Chunker {

  def read(readStream: ReadStream,onClosed:Array[String] => Unit) = {
    val interface: Interface = readline.createInterface(readStream)
    var  seq:Array[String] = Array()
    interface.on("line",(s)=>{seq = (seq :+ s.toString)})
    interface.on("close",(e)=>onClosed(seq))
  }


  def toPars(lines: Array[String]): Map[Int, Array[String]] = {
    var counter = 0;
    def isHeader(s: String) = s.trim.startsWith("#")

    lines.groupBy[Int]((line: String) => {
      if (isHeader(line))
        counter = counter + 1
      counter
    })

  }


}
