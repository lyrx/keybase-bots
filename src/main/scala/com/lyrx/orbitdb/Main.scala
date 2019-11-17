package com.lyrx.orbitdb

import scala.scalajs.js
import js.annotation.{JSExport, JSExportTopLevel}
import js.Dynamic.{literal=>l}
@JSExportTopLevel("Orbit")
object Main {

  @JSExport
  def initt(): Unit = {

    val client = IpfsHttpClient("localhost",5001)
    println("youchai");
  }

}
