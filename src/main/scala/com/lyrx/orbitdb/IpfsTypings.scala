package com.lyrx.orbitdb

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport



@js.native
trait IpfsClient extends js.Object {

}


@js.native
@JSImport("ipfs-http-client",JSImport.Namespace)
object IpfsHttpClient extends js.Object {

  def apply(lit:js.Dynamic):IpfsClient = js.native
  def apply(host:String,port:String):IpfsClient = js.native

}
