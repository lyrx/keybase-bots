package com.lyrx.text.processing

case class Section(level: Int,
                   index: Int,
                   metaData: MetaData,
                   pages: Seq[PageSnippet],
                   titleOpt: Option[String])
