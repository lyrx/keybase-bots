package com.lyrx.text.processing

case class Section(level: Int,
                   index: Int,
                   metaData: MetaData,
                   pagesOpt: Option[Array[PageSnippet]],
                   titleOpt: Option[String])
