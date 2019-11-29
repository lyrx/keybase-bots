package com.lyrx.text.processing

import com.lyrx.text.processing.Types._
import typings.node.NodeJS.ErrnoException

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.|
import typings.node

import node.{fsMod => fs}
trait Markdown {

  val taking: Taking

  def listMarkdownFrags()(implicit executionContext: ExecutionContext) = {
    val promise = Promise[js.Array[String]]()
    taking.mdOutputPathOpt.map(
      path =>
        fs.readdir(path, (e: ErrnoException | Null, r: js.Array[String]) => {
          ()
        })
    )
    promise.future
  }

}
