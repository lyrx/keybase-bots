package com.lyrx.text.processing.books

import com.lyrx.text.processing.Types.HeaderDetection

trait Constants {
  val output = "/Users/alex/output"
  val GATEWAY="ipfs.lyrx.de"
  def SNIPPETS()="/ipns/QmS9RqAEWd4fKNiDCRgDKnWT3mDB5q9VkXsZJFCcw5gya5"


  val h: HeaderDetection = (line) => {
    val s = line.trim

    if (s.startsWith("####"))
      4
    else if (s.startsWith("###"))
      3
    else if (s.startsWith("##"))
      2
    else if (s.startsWith("#"))
      1
    else
      0
  }

}
