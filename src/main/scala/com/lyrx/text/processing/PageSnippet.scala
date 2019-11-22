package com.lyrx.text.processing

case class PageSnippet(
                        markdownFileOpt: Option[String],
                        hashOpt: Option[String],
                        htmlOpt: Option[String]
)
