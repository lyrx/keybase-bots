package com.lyrx.text.processing

import com.lyrx.text.processing.Types.Lines
import com.lyrx.text.processing.filter.{Filters, LineFilter, LinesFromFile}

import scala.concurrent.{ExecutionContext, Future}

trait CollectorFilter extends LinesFromFile {
  val taking: Taking


  def beautifyLines()=new Taker(taking.
    copy(linesOpt = taking.linesOpt.map(
      lines => {
        Filters.concatFilters(
          Filters.FOLDLINES,
          Filters.TRIMLINES,
          Filters.REDUCE
        )(
          lines)
      })
    ))



  def collectMarkdown(file:String,
              marks:Seq[String])(implicit executionContext: ExecutionContext)={
    val collection=collectMarkdownFrom(s"${file}")
    marks.foldLeft(collection:Future[Taker])(
      (f,mark)=>f.map(_.fromMark(mark))
    ).map(_.beautifyLines())
  }

  def collectMarkdownMarks(file:String,prefix:String)(implicit executionContext: ExecutionContext)=
    collectMarkdown(
    file,
    (1 to 40).map(num=>{
      s"${prefix}${num}"
    })
  )



  def withFilter(f: Lines => Lines) =
    new Taker(taking.copy(filterOpt = Some(f)))

  def collectMarkdownFrom(s: String)(implicit executionContext: ExecutionContext) =
    fromFile(s).map(lines =>
      new Taker(taking.copy(linesCollectorOpt = Some(Filters.MARKDOWN(lines)))))

  def title(title:String)=
    takeMarkdown(Seq(s"# ${title} #"))

  def img(src:String,descr:String)=
    takeMarkdown(Seq(
      s"![${descr}](${src})",
      "\n"
    ))


  def takeMarkdown(lines:Seq[String])=
    new Taker(taking.copy(linesOpt =
      Some(
        taking.linesOpt.getOrElse(Seq())
          ++ lines
      )))



  def fromMark(mark: String): Taker =
    applyFilter(
          Filters.filterMarker(mark) _)

  def all(): Taker =
    applyFilter(Filters.ALL)

  def applyFilter(f: Lines=>Lines): Taker =
    new Taker(
      taking.copy(
        filterOpt = Some(f))).
      applyCollectorFilter()




  def applyCollectorFilter() =
    taking.filterOpt
      .flatMap(
        filter =>
          taking.linesCollectorOpt.map(
            collectorLines =>
              new Taker(taking.copy(linesOpt = Some(
                taking.linesOpt.getOrElse(Seq()) ++ filter(collectorLines))))
        ))
      .getOrElse(new Taker(taking))

  def filter(): Taker =
    taking.linesOpt
      .flatMap(lines =>
        taking.filterOpt.map(filter =>
          new Taker(taking.copy(linesOpt = Some(filter(lines))))))
      .getOrElse(new Taker(taking))

}
