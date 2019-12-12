package com.lyrx.text.processing

import scala.concurrent.{ExecutionContext, Future}

trait HTML2 extends Generator with IOTrait{

  val taking: Taking

  def htmlFragPath(aId: String) = s"${taking.outPath}/html/${aId}"


  def writeHTML(inFile:String)(implicit executionContext: ExecutionContext) =
    taking.idOpt
      .map(id =>
        mmkdirp(htmlFragPath(id)).flatMap(dir =>
          pandoc(inFile,s"${dir}/${id}.html")
          .map(r=>new Taker(taking))
        )).getOrElse(Future{new Taker(taking)})




  def writeHTMLs()(implicit executionContext: ExecutionContext) =
    taking.idOpt
      .map(id =>
        mmkdirp(htmlFragPath(id)).flatMap(dir =>
          Future
            .sequence(taking.mdFrags.map(mdFrag => {
              val baseName =
                mdFrag.stripSuffix(".md").substring(mdFrag.lastIndexOf('/') + 1)
              pandoc(mdFrag, s"${dir}/${baseName}-frag.html")
            }))
            .map(it => new Taker(taking.copy(htmlFrags = it)))))
      .getOrElse(Future { new Taker(taking) })

}
