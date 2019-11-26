package com.lyrx.text.processing.filter

import com.lyrx.text.processing.Types.Lines
import com.lyrx.text.processing.filter.Types.FilterFunc
import typings.node
import node.{fsMod => fs, readlineMod => readline}
import scala.concurrent.Future


trait CombinedLineFilter  extends MarkerFilter with LineNumberFilter {
  val filters:Seq[FilterFunc]

  def filterLines(in: Seq[String]): Seq[String] = filters.
    foldLeft(in: Seq[String])((r,t)=>t(r))


  def :+(filter:FilterFunc): CombinedLineFilter = new
      CombinedLineFilterImpl(filters :+ filter)

  def :+(from:Int,to:Int): CombinedLineFilter = new
      CombinedLineFilterImpl(filters :+ filterLineNumbers(from,to) _ )
  def :+(marker:String): CombinedLineFilter = new
      CombinedLineFilterImpl(filters :+ filterMarks(marker) _ )



}

object  CombinedLineFilterImpl{


  def apply() = new CombinedLineFilterImpl(Seq())

  /*
    def lineFilter(
                    file:String,
                    from:Int,
                    to:Int)(implicit
                  executionContext: ExecutionContext
    ): Future[Seq[String]] = lines(fs.
      createReadStream(file)).map(lines=>
      new LineNumberFilterImpl(from,to).filterLines(lines))




    def filterLines(
                    lines:Lines,
                    from:Int,
                    to:Int)(implicit
                            executionContext: ExecutionContext
                  )=
      new LineNumberFilterImpl(from,to).filterLines(lines)




   */





  def lines(readStream: fs.ReadStream): Future[Lines] = {
    val interface: readline.Interface = readline.createInterface(readStream)
    var seq: Seq[String] = Seq()
    val promise = concurrent.Promise[Lines]()
    interface.on("line", (s) => { seq = (seq :+ s.toString) })
    interface.on("close", (e) => {
      promise.success(seq)
      ()
    })
    promise.future
  }
}

class CombinedLineFilterImpl(override val filters:Seq[FilterFunc]) extends CombinedLineFilter{

}
