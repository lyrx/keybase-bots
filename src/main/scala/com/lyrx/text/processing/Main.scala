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
  val haesschen = s"${resources}/briefe/haesschenbriefe"

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

   // karlMayBooks.map(chunk(_))
    // traktatus
  // doDickens()
   // generate(s"${books}/FriedrichSchiller")("aesthetik")
   // doKoblach
   // doDiary()
   //  doHegel()
    haselis()

  }


  def haselis()= {
    val t = Taker().id("haesschen")

    (1 to 3).
      foldLeft(Future{t}:Future[Taker])(
     (f,i)=>doHaesschen(f,i)
      )
      .flatMap(_.
        writeToPath(s"${haesschen}/haesschenbriefe.md"))
  }





  def doHaesschen(aFutureTaker:Future[Taker],num:Int) = aFutureTaker.
    flatMap(aTaker=> aTaker.
    collectFrom(
    s"${haesschen}/elias${num}.md"
  ).map(_.
    title(s"Brief ${num}").
    applyFilter(Filters.MARKDOWN)
  ))



  private def doHegel(){
    generate(
      s"${books}/GeorgWilhelmFriedrichHegel")(
      "phnomenologiedesgeistes")

  }
  private def doKoblach() = {
    Taker()
      .id("koblach")
      .collectFrom(s"${koblach}/novel.md")
      .flatMap(_.fromMark("t1").
        writeToPath(s"${koblach}/whois.md").
        flatMap(t=>t.mdAndHTML())
      )

  }

  private def doDiary() =
    Taker()
      .id("diary")
      .collectFrom(s"${diary}/ttagebuch.md")
      .flatMap(
        _.title("Sonntag, erster Dezember 2019")
          .img(
            "images/IMG_1948.jpeg",
            "Weihnachtsbaum des Bonifatius-Heims"
          )
          .fromMark("d1")
          .writeToPath(s"${diary}/mydiary.md")
          .flatMap(t=>t.mdAndHTML())
      )


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
