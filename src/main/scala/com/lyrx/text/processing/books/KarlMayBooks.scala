package com.lyrx.text.processing.books

import com.lyrx.text.processing.Book

trait KarlMayBooks {

  val books:String
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


  val mayRoot = s"${books}/KarlMay"
  def may(aName: String) = Book.from(aName, mayRoot)

}
