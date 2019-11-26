package com.lyrx.text.processing.filter

import com.lyrx.text.processing.Types.Lines

import scala.concurrent.Future
import typings.node
import node.{fsMod => fs, readlineMod => readline}

trait LinesFromFile {

  def writeLines(file: String, lines: Lines) = {
    val promise = concurrent.Promise[String]()
    fs.writeFile(
      file,
      lines.mkString("\n"),
      (e) => {
        promise.success(file)
        ()
      }
    )
    promise.future
  }

  def fromFile(file: String) = lines(fs.createReadStream(file))

  def lines(readStream: fs.ReadStream): Future[Lines] = {
    val interface: readline.Interface = readline.createInterface(readStream)
    var seq: Seq[String] = Seq()
    val promise = concurrent.Promise[Lines]()
    interface.on("line", (s) => { seq = (seq :+ s.toString) })
    interface.on("close", (e) => {
      promise.success(seq)
      ()
    })
    promise.future
  }

}
