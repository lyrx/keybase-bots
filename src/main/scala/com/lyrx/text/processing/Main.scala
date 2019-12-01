package com.lyrx.text.processing

import com.lyrx.text.processing.Types.HeaderDetection
import com.lyrx.text.processing.filter.Filters
import typings.node
import node.{fsMod => fs}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
@JSExportTopLevel("Chunker")
object Main extends Chunker {

  implicit val exc = ExecutionContext.global

  val resources =
    "/Users/alex/git/texte/projects/lyrxgenerator/src/main/resources"
  val creative = s"${resources}/creative"
  val kuendigung = s"${creative}/kuendigung"
  val koblach = s"${kuendigung}/koblach"
  val ideen = s"${kuendigung}/ideen"
  val diary = s"${kuendigung}/diary"
  val pyramids = s"${kuendigung}/pyramids"

  val books = s"${resources}/books"
  val mayRoot = s"${books}/KarlMay"
  val hegel = s"${books}/GeorgWilhelmFriedrichHegel"
  val dickens = s"${books}/CharlesDickens"
  val schiller = s"${books}/FriedrichSchiller"

  val output = "/Users/alex/output"
  val h: HeaderDetection = (line) => {
    val s = line.trim

    if (s.startsWith("####"))
      4
    else if (s.startsWith("###"))
      3
    else if (s.startsWith("##"))
      2
    else if (s.startsWith("#"))
      1
    else
      0
  }
  def karlMayBooks(): Seq[Book] =
    Seq(
      "amjenseits.md",
      "durchdiewste.md",
      "indencordilleren.md",
      "satanundischariotii.md",
      "amriodelaplata.md",
      "durchswildekurdistan.md",
      "oldsurehandi.md",
      "satanundischariotiii.md",
      "amstillenocean.md",
      "imlandedesmahdii.md",
      "oldsurehandii.md",
      "undfriedeauferden.md",
      "auffremdenpfaden.md",
      "imlandedesmahdiii.md",
      "oldsurehandiii.md",
      "vonbagdadnachstambul.md",
      "dermirvondschinnistan.md",
      "imlandedesmahdiiii.md",
      "orangenunddatteln.md",
      "weihnacht.md",
      "dermirvondschinnistani.md",
      "imreichdessilbernenlweniii.md",
      "satanundischariotbandi.md",
      "winnetoui.md",
      "dermirvondschinnistanii.md",
      "imreichdessilbernenlweniv.md",
      "satanundischariotbandii.md",
      "winnetouii.md",
      "derschut.md",
      "imreichedessilbernenlweni.md",
      "satanundischariotbandiii.md",
      "winnetouiii.md",
      "durchdaslandderskipetaren.md",
      "imreichedessilbernenlwenii.md",
      "satanundischarioti.md",
      "winnetouiv.md"
    ).map(_.stripSuffix(".md")).map(may(_))

  def may(aName: String) = Book.from(aName, mayRoot)

  def doDickens() = {
    val d = generate(dickens) _
    Seq("greatexpectations", "littledorrit", "olivertwist").map(d(_))

  }

  @JSExport
  def initt() = {
    //generate(hegel)("phnomenologiedesgeistes")
    //karlMayBooks.map(chunk(_))
    //traktatus
    //doDickens()
    //generate(s"${books}/FriedrichSchiller")("aesthetik")
    doKoblach
    doDiary()
  }

  private def doKoblach() = {
    Taker()
      .id("koblach")
      .withOutPath(s"${output}/kuendigung")
      .collectFrom(s"${koblach}/novel.md")
      .map(_.fromMark("t1").writeToFile(s"whois.md"))
  }

  private def doDiary() =
    Taker()
      .id("diary")
      .withOutPath(s"${output}/diary")
      .collectFrom(s"${diary}/ttagebuch.md")
      .map(
        _.title("Sonntag, erster Dezember 2019")
          .fromMark("d1")
          .writeToFile(s"mydiary.md"))


  private def traktatus = {
    Taker()
      .mdPath(s"${books}/LudwigWittgenstein/tractatus.md")
      .id("traktatus")
      .withFilter(Filters.tractatus)
      .readMD()
      .flatMap(_.filter().mdAndHTML())
  }

  def generate(base: String)(id: String) =
    Taker().id(id).mdPath(s"${base}/${id}.md").readMD().flatMap(_.mdAndHTML())

  private def chunk(book: Book) =
    book
      .withMarkdownSections()
      .flatMap(
        b => {
          //b.sections.map(section => println(section))
          b.writeMarkdownChunks(30)
            .map(
              b2 =>
                b2.writeHTMLChunks()
                  .map(b3 => println(s"Processed ${b3.context.metaData.name}")))
        }
      )

}
