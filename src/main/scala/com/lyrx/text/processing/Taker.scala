package com.lyrx.text.processing

import com.lyrx.text.processing.Types.Lines

case class Taking(
                   currentPathOpt: Option[String] = None,
                   idOpt:Option[String] = None,
                   slizeOpt:Option[Int] = None,
                   linesOpt:Option[Lines] = None
                 )

object Taker{
  def apply()=new Taking()
}

class Taker(taking: Taking) {

  def mdPath(s: String) =
    new Taker(taking.copy(currentPathOpt = Some(s)))

  def id(s: String) =
    new Taker(taking.copy(idOpt = Some(s)))

  def slize(num:Int) =
    new Taker(taking.copy(slizeOpt = Some(num)))


  def readMD() =   ???



}
