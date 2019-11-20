package com.lyrx.text.processing

import com.lyrx.text.processing.Types.HeaderDetection

case class Context(
    headerLevel: HeaderDetection,
    metaData: MetaData,
    outPath: String,
    markdownSourceOpt:Option[String]
)
