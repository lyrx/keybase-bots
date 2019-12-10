package com.lyrx.text.processing.filter

import com.lyrx.text.processing.Types.Lines

object Filters {

  type MAPPING = Lines => Lines
  val R1 = """:\s+(.*)""".r
  val R11 = """\s+(.*)""".r
  val R2 = """\[(\d+)\].*""".r
  val R3 = """\[(\d+\.\d)\].*""".r
  val R4 = """\[(\d+\.\d\d)\].*""".r
  val R5 = """\[(\d+\.\d\d\d)\].*""".r
  val R6 = """\[(\d+\.\d\d\d\d)\].*""".r
  val R7 = """\[(\d+\.\d\d\d\d\d)\].*""".r

  val IMGR = """\s*img\s+([\w\d.]+).*""".r
  val YOUTUBE = """\s*youtube\s+([\w\d.]+).*""".r

  implicit class PimmpedMapping(m: MAPPING) {

    def ->(other: MAPPING) = concatFilters(m, other)

  }

  val tractatus: Lines => Lines = (lines: Seq[String]) =>
    lines
      .map(line =>
        line match {
          case R1(aline: String) => Seq[String]("\n", aline)
          case R11(aline)        => Seq[String](aline)
          case R2(num)           => Seq[String](s"# ${num} #")
          case R3(num)           => Seq[String](s"## ${num} ##")
          case R4(num)           => Seq[String](s"### ${num} ###")
          case R5(num)           => Seq[String](s"#### ${num} ####")
          case R6(num)           => Seq[String](s"##### ${num} #####")
          case R7(num)           => Seq[String](s"###### ${num} ######")
          case _                 => Seq[String](line)
      })
      .flatten

  def MARKDOWN: MAPPING = concatFilters(
    MDSKIPDASHES,
    IMG,
    YOUTUBEFILTER)

  def MDSKIPDASHES: Lines => Lines =
    (s) =>
      s.foldLeft(
          (Seq(), false): (Lines, Boolean)
        )(
          (t, line) => {
            val dashes = line.startsWith("---")
            val omit: Boolean = if (dashes) (!t._2) else t._2
            (if (dashes || omit) t._1 else (t._1 :+ line), omit)
          }
        )
        ._1

  def concatFilters(filters: MAPPING*) =
    (lines: Lines) =>
      filters.foldLeft(lines: Lines)((in: Lines, f: MAPPING) => f(in))

  def ALL: MAPPING = (lines) => lines

  def TRIMLINES: MAPPING = //chop unneeded empty lines in beginning and end
    (s) =>
      s.dropWhile(_.trim.length == 0)
        .reverse
        .dropWhile(_.trim.length == 0)
        .reverse

  def REDUCE: MAPPING = //remove redundant empty lines
    (s) =>
      s.foldLeft(Seq(): Lines)((in: Lines, line: String) => {
        val lastEmpty: Boolean =
          in.lastOption.map(_.trim.length() == 0).getOrElse(false)
        if (lastEmpty && line.trim.length == 0)
          in
        else
          in :+ line
      })

  val FOLDLINES: MAPPING = (lines: Lines) => { // one line per paragraph

    def isTextLine(s: String) = (
      !s.startsWith("\t") &&
        !s.startsWith("     ") &&
        !s.trim.startsWith("#") &&
        !s.matches("""^\s*\d+\.?\s+.*""")
    )
    lines.foldLeft(Seq(): Lines)((llines: Lines, line: String) =>
      if (line.trim.length == 0) {
        (llines ++ Seq(line, line))
      } else {
        if (isTextLine(line)) {
          val concatOpt = llines.lastOption.map(
            lastLine =>
              (lastLine +
                (if (lastLine.trim.length > 0) " " else "") +
                line))
          concatOpt
            .map(concat => llines.dropRight(1) :+ concat)
            .getOrElse(llines :+ line)
        } else
          llines :+ line

    })
  }

  def CHAPTER(contains:String):MAPPING = (seq)=>{
    val r = seq.
      dropWhile(line=> !(line.trim.startsWith("#") &&  line.contains(contains)) )


    r.head +: r.tail.takeWhile( line => !(line.trim.startsWith("#")))
  }

  def IMG: MAPPING =
    (s) =>
      s.map(
        line =>
          line match {
            case IMGR(name) => s"![](images/${name})"
            case _          => line
        }
    )


  val YOUTUBEFILTER: MAPPING =
    (s) =>
      s.map(
        line =>
          line match {
            case YOUTUBE(name) => s"""<iframe
                                     |src="https://www.youtube.com/embed/${name}?autoplay=1"
                                     |frameborder="0"
                                     |allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
                                     |allowfullscreen></iframe>""".stripMargin
            case _          => line
          }
      )







  def prefixer(marker: String,file:String)(in: Seq[String]) =if(
    !in.isEmpty)
    s"**[[${marker}:${file}]]**" +: in
  else
    in


  def filterMarker(marker: String)(in: Seq[String]): Seq[String] =
    in.dropWhile(!_.matches(marker)).drop(1).takeWhile(!_.matches(marker))

}
