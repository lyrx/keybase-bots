package com.lyrx.keybase

import typings.node.process


object Main {

  def main(args:Array[String]) = {
    println( s"Hello keybase: ${process.env("KB_USERNAME")}");



  }

}
