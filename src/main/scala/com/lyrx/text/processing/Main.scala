package com.lyrx.text.processing

import com.lyrx.text.processing.Types.HeaderDetection
import typings.node
import node.{fsMod => fs}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
@JSExportTopLevel("Chunker")
object Main extends Chunker {

  implicit val exc = ExecutionContext.global

  val books= "/Users/alex/git/texte/projects/lyrxgenerator/src/main/resources/books"
  val mayRoot = s"${books}/KarlMay"
  val hegel=s"${books}/GeorgWilhelmFriedrichHegel"


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
  val ctxs = Seq(
    /*
    "satanundischarioti",
    "satanundischariotii",
     "satanundischariotiii",
    "winnetoui",
    "winnetouii",
    "winnetouiii",
    "winnetouiv",



    "imlandedesmahdii",
    "imlandedesmahdiii",
    "imlandedesmahdiiii",
    "imreichdessilbernenlweniii",
    "imreichdessilbernenlweniv",
    "dermirvondschinnistani",
    "dermirvondschinnistanii",
    "durchdaslandderskipetaren",
    "durchdiewste",
    "durchswildekurdistan",
    "undfriedeauferden",
    "vonbagdadnachstambul",
    "weihnacht",
    "derschut",

     */
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
  @JSExport
  def initt() = {
    val id = "phnomenologiedesgeistes"
    Taker().
      id(id).
      mdPath(s"${hegel}/${id}.md").
      readMD().flatMap(_.toSections().map(sequence=>
      Future.sequence(sequence.map(
        taker=>taker.slize(30).
          grouping().
          writeMarkdowns().map(_.writeHTMLs()))
      ))
    ).flatten.
      map(it=>Future.sequence(it)).flatten
  }




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
