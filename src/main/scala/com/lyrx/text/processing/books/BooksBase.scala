package com.lyrx.text.processing.books

import com.lyrx.text.processing.{Book, Taker}
import com.lyrx.text.processing.Types.HeaderDetection

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

trait BooksBase {
  val output = "/Users/alex/output"


  implicit val exc = ExecutionContext.global
  val resources =
    "/Users/alex/git/texte/projects/lyrxgenerator/src/main/resources"

  val creative = s"${resources}/creative"
  val kuendigung: String = s"${creative}/kuendigung"
  val ideen = s"${kuendigung}/ideen"
  val pyramids = s"${kuendigung}/pyramids"
  val books = s"${resources}/books"
  val kind = s"${creative}/derjunge"



  implicit class FutureTaker(taker:Future[Taker]){

    def col(file:String,prefix:String)(implicit aroot:String)=
      taker.flatMap(_.
        collectMarkdownMarks(
          if(file.startsWith("/"))
            s"${file}"
          else
            s"${aroot}/${file}"
          ,
          prefix
        ))
  }



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

  def generate(base: String)(id: String): Future[immutable.Iterable[Taker]] =
    Taker().id(id).mdPath(s"${base}/${id}.md").readMD().flatMap(_.mdAndHTML())

   def chunk(book: Book): Future[Future[Unit]] =
    book
      .withMarkdownSections()
      .flatMap(
        _.writeMarkdownChunks(30)
            .map(
              b2 =>
                b2.writeHTMLChunks()
                  .map(b3 => println(s"Processed ${b3.context.metaData.name}")))

      )

}
