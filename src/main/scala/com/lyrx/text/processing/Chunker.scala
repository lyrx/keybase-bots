package com.lyrx.text.processing

import typings.node.childUnderscoreProcessMod.spawn
import typings.node.fsMod.ReadStream

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js

trait Chunker extends Grouping with HTML {

  def sectionsToHTML(readStream: ReadStream, context: Context)(
      implicit ctx: ExecutionContext) =
    toFiles(
      readStream,
      context
    ).map(_.map(sectionToHTML(_)))
      .map(Future.sequence(_)).
      flatten.map(_.toSeq.flatten)






}
