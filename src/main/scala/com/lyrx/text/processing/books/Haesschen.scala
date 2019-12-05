package com.lyrx.text.processing.books

import com.lyrx.text.processing.Taker
import com.lyrx.text.processing.filter.Filters

import scala.concurrent.{ExecutionContext, Future}

trait Haesschen {

  implicit val exc:ExecutionContext

  val resources:String
  val haesschen = s"${resources}/briefe/haesschenbriefe"


  def haselis()= {
    val t = Taker().id("haesschen")

    (1 to 20).
      foldLeft(Future{t}:Future[Taker])(
        (f,i)=>doHaesschen(f,i)
      )
      .flatMap(_.
        writeToPath(s"${haesschen}/haesschenbriefe.md"))
      .flatMap(
        _.mdAndHTML()
      )
  }





  def doHaesschen(aFutureTaker:Future[Taker],num:Int) = aFutureTaker.
    flatMap(aTaker=> aTaker.
      collectMarkdownFrom(
        s"${haesschen}/elias${num}.md"
      ).map(_.
      title(s"Brief ${num}")
    ))

}
