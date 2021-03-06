package com.lyrx.text.processing.books
import com.lyrx.text.processing.Taker
import scala.concurrent.Future

trait Pyramids extends BooksBase {
  override def cid() = "pyramids"
  override implicit val withPrefix: Boolean = false
  implicit override val aroot: String = s"${kuendigung}/${cid()}"

  override def collect(
      )(implicit aroot: String, withPrefix: Boolean):
       Future[Taker] =
    taker
      .coll(s"${aroot}/usecase.md", "sss")

}
