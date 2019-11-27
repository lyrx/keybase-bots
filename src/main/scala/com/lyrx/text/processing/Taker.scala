package com.lyrx.text.processing

import com.lyrx.text.processing.Types.{Lines, Page, PageMap}
import com.lyrx.text.processing.filter.LinesFromFile
import typings.mkdirp.mkdirpMod.{Made, ^ => mkdirp}
import typings.node.NodeJS.ErrnoException


import scala.concurrent.{ExecutionContext, Future}
case class Taking(
                   mdPathOpt: Option[String] = None,
                   idOpt:Option[String] = None,
                   slizeOpt:Option[Int] = None,
                   linesOpt:Option[Lines] = None,
                   mapOpt:Option[PageMap] = None,
                   outPath:String = "/Users/alex/output"
                 )

object Taker{
  def apply()=new Taker(Taking())
}

class Taker(override val taking: Taking) extends LinesFromFile with Grouping2 with Writer{

  def mdPath(s: String) =
    new Taker(taking.copy(mdPathOpt = Some(s)))

  def id(s: String) =
    new Taker(taking.copy(idOpt = Some(s)))

  def slize(num:Int) =
    new Taker(taking.copy(slizeOpt = Some(num)))


  def readMD()(implicit executionContext: ExecutionContext) =   taking.
    mdPathOpt.
    map(path=>fromFile(path).map(
      lines=>
        new Taker(taking.copy(linesOpt = Some(lines))))).
    getOrElse(Future{Taker.this})



  def grouping()=taking.linesOpt.
    flatMap(lines=>taking.slizeOpt.map(
      slize=>new Taker(taking.copy(mapOpt=Some(group(lines,slize))))
    )).getOrElse(Taker.this)


  def writeMarkdowns()(implicit executionContext:ExecutionContext)=taking.idOpt.map(
    id=>{
      val promise = concurrent.Promise[Taker]()
      val markdownOutputDir =
        s"${taking.outPath}/markdown/${id}"
      mkdirp(
        markdownOutputDir,
        (e: ErrnoException, m: Made) => if (e != null)
            promise.failure(e.asInstanceOf[Throwable])
          else
            ppagesToFiles(markdownOutputDir).
              map(r=>promise.success(Taker.this))
        )
    promise.future
    }
  ).getOrElse(Future{Taker.this})











}
