package com.lyrx.text.processing.books

import com.lyrx.text.processing.{Book, Taker}


import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

trait BooksBase extends Constants {



  implicit val withPrefix:Boolean
  def collect()(implicit aroot:String,withPrefix:Boolean):Future[Taker]
  def cid():String


  implicit val exc = ExecutionContext.global

  val resources =
    "/Users/alex/git/texte/projects/lyrxgenerator/src/main/resources"
  val books = s"${resources}/books"

  val creative = s"${resources}/creative"
  val kind = s"${creative}/derjunge"

  val kuendigung: String = s"${creative}/kuendigung"
  val ideen = s"${kuendigung}/ideen"
  val pyramids = s"${kuendigung}/pyramids"

  implicit val aroot: String








   val taker: Taker = Taker().id(cid())



  def generate() = collect().finish()


  implicit class FutureTaker(taker:Future[Taker]){

    def finish()(
      implicit aroot:String) = {
      val generated = s"${aroot}/generated.md"
      taker.flatMap(_.
        beautifyLines().
        writeToPath(generated).
        flatMap(_.writeHTML(generated))
      ).flatMap(_.mdAndHTML())
    }


    def title(s:String) = taker.map(
      _.title(s)
    )

    def col(file:String,prefix:String)(
      implicit aroot:String,
      withPrefix:Boolean
    )=
      taker.flatMap(_.
        coll(
          expandFile(file)
          ,
          prefix
        ))


    def allFrom(file:String)(
      implicit aroot:String
    )=taker.flatMap(t=>t.allFrom(
      expandFile(file)
    ))

    private def expandFile(file:String)(
                          implicit aroot:String)
    =  if(file.startsWith("/"))
      s"${file}"
    else
      s"${aroot}/${file}"



    def chapter(afile:String,achapter:String)(
      implicit aroot:String,hasPrefix:Boolean
    )=taker.flatMap(_.collectChapter(
     file = expandFile(afile),
        chapter = achapter,
        hasPrefix))

    def chapter(achapter:String)(
      implicit aroot:String,hasPrefix:Boolean
    )=taker.map(_.collectChapter(
      chapter = achapter,
      hasPrefix))




    def take(file:String)(
      implicit aroot:String) = taker.flatMap(
      _.collectMarkdownFrom(expandFile(file))
    )


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
