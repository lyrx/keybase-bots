package com.lyrx.text.processing

import typings.node.childUnderscoreProcessMod.spawn

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js

trait HTML {



  //pandoc /Users/alex/output/satanundischarioti/satanundischarioti_1_0.md -o /Users/alex/output/satanundischarioti/satanundischarioti_1_0-frag.html
  def toHTML(pageSnippet: PageSnippet)(implicit ctx: ExecutionContext) =
    pageSnippet.fileOpt
      .map(file => {
        val promise = concurrent.Promise[PageSnippet]
        val base = file.stripSuffix(".md")
        val html = s"${base}-frag.html"
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

  def toHTMLs(a: Array[PageSnippet])(implicit ctx: ExecutionContext) =
    a.foldLeft(Future { Array() }: Future[Array[PageSnippet]])((f, s) =>
      f.flatMap(snippets => toHTML(s).map(p => snippets :+ p)))

  def sectionToHTML(section: Section)(implicit ctx: ExecutionContext) =
    section.pagesOpt
      .map(a => toHTMLs(a).map(pages => section.copy(pagesOpt = Some(pages))))
      .getOrElse(Future { section })



}
