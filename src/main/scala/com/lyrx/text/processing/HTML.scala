package com.lyrx.text.processing

import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node.NodeJS.ErrnoException
import typings.node.childUnderscoreProcessMod.spawn

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js

trait HTML {



  //pandoc /Users/alex/output/satanundischarioti/satanundischarioti_1_0.md -o /Users/alex/output/satanundischarioti/satanundischarioti_1_0-frag.html
  def toHTML(pageSnippet: PageSnippet,outPath:String)(implicit ctx: ExecutionContext) =
    pageSnippet.markdownFileOpt
      .map(file => {
        val promise = concurrent.Promise[PageSnippet]
        val html = s"${outPath}/${pageSnippet.name}-frag.html"
        spawn("pandoc", js.Array(file, "-o", html)).on(
          "close",
          (code) => {
            promise.success(pageSnippet.copy(htmlOpt = Some(html)))
            ()
          }
        )
        promise.future
      })
      .getOrElse(Future { pageSnippet })

  def toHTMLs(
               a: Seq[PageSnippet],
               context:Context)(
    implicit ctx: ExecutionContext): Future[Seq[PageSnippet]] = {
    val htmlOutputDir = s"${context.outPath}/html"
    val promise = Promise[Future[Seq[PageSnippet]]]()
    mkdirp(
      htmlOutputDir,
      (e: ErrnoException, m: Made) => {
        if(e != null){promise.failure(e.asInstanceOf[Throwable])}

        else {
          promise.success(
            a.foldLeft(Future { Seq() }: Future[Seq[PageSnippet]])((f, s) =>
              f.flatMap(snippets => toHTML(s,htmlOutputDir).map(p => snippets :+ p)))
          )
        }})

     promise.future.flatten
  }

  def sectionToHTML(
                     section: Section,
                     context:Context)(
    implicit ctx: ExecutionContext) =
    toHTMLs(section.pages,context)


}
