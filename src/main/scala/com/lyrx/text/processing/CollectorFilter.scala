package com.lyrx.text.processing

import com.lyrx.text.processing.Types.Lines
import com.lyrx.text.processing.filter.{Filters, LineFilter, LinesFromFile}

import scala.concurrent.{ExecutionContext, Future}
import Filters._

trait CollectorFilter extends LinesFromFile {
  val taking: Taking

  def beautifyLines() =
    new Taker(taking.copy(linesOpt = taking.linesOpt.map(lines =>
      (FOLDLINES -> TRIMLINES -> REDUCE)(lines))))



  def withPrefix(p: String) =
    new Taker(
      taking.copy(
        linesCollectorOpt = taking.linesCollectorOpt.map(lines => s"${p}" +: lines)
      ))

  def collectMarkdown(file: String, marks: Seq[String], prefix: Boolean)(
      implicit executionContext: ExecutionContext) = {
    val collection: Future[Taker] = collectMarkdownFrom(s"${file}")
    marks
      .foldLeft(collection: Future[Taker])(
        (f, mark) => f.map(_.fromMark(mark, file, prefix))
      )
  }

  def collectChapter(file: String, chapter: String, prefix: Boolean)(
    implicit executionContext: ExecutionContext) = {
    val collection: Future[Taker] = collectMarkdownFrom(s"${file}")
    collection.map(t=>t.fromChapter(
      chapter,file,prefix
    ))
  }

  def collectChapter(chapter: String, prefix: Boolean)(
    implicit executionContext: ExecutionContext) = {
    fromChapter(
      chapter,taking.mdInputPathOpt.getOrElse(""),prefix
    )
  }



  def collectMarkdownMarks(file: String, prefix: String)(
      implicit executionContext: ExecutionContext,
      withPrefix: Boolean) =
    collectMarkdown(
      file,
      (1 to 40).map(num => {
        s"${prefix}${num}"
      }),
      withPrefix
    )

  def allFrom(file: String)(implicit executionContext: ExecutionContext,
                            withPrefix: Boolean) =
    collectMarkdownFrom(file)
      .map(
        t =>
          if (withPrefix)
            t.withPrefix(s"[[${file}]]")
          else
          t).
      map(_.all())

  def withFilter(f: Lines => Lines) =
    new Taker(taking.copy(filterOpt = Some(f)))

  def collectMarkdownFrom(s: String)(
      implicit executionContext: ExecutionContext) =
    fromFile(s).map(lines =>
      new Taker(taking.copy(
        linesCollectorOpt = Some(MARKDOWN(lines)),
        mdInputPathOpt =Some(s)
      )))

  def title(title: String) =
    takeMarkdown(Seq(s"# ${title} #"))

  def img(src: String, descr: String) =
    takeMarkdown(
      Seq(
        s"![${descr}](${src})",
        "\n"
      ))

  def takeMarkdown(lines: Seq[String]) =
    new Taker(
      taking.copy(
        linesOpt = Some(
          taking.linesOpt.getOrElse(Seq())
            ++ lines
        )))

  def fromMark(amark: String, afile: String, aprefix: Boolean): Taker =
    fromFilter(
      mark = amark,
      file = afile,
      prefix =aprefix,
      filter =(filterMarker(amark) _)
    )


  def fromChapter(chapter: String, afile: String, aprefix: Boolean): Taker =
    fromFilter(
      mark = chapter,
      file = afile,
      prefix = aprefix,
      filter = CHAPTER(chapter))



  def fromFilter(
                  mark: String,
                  file: String,
                  prefix: Boolean,
                  filter:MAPPING
                ): Taker =
    if (prefix)
      applyFilter(
        filter
          ->
          (prefixer(mark, file) _)
      )
    else
      applyFilter(
        filter
      )




  def all(): Taker =
    applyFilter(Filters.ALL)

  def applyFilter(f: Lines => Lines): Taker =
    new Taker(taking.copy(filterOpt = Some(f))).applyCollectorFilter()

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
