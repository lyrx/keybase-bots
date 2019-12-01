package com.lyrx.text.processing

import com.lyrx.text.processing.Types._
import typings.node.NodeJS.ErrnoException

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.|
import typings.node

import typings.node.childUnderscoreProcessMod.spawn
trait Generator {

  val taking: Taking


  def pandoc(inFile:String,outFile:String)(implicit ctx: ExecutionContext) ={
    val promise = concurrent.Promise[String]
    spawn("pandoc",
      js.Array(inFile, "-o", outFile)).
      on(
        "close",
        (code) => {
          promise.success(outFile)
          ()
        }
      )
    promise.future
  }




}
