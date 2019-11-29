package com.lyrx.text.processing.filter

import com.lyrx.text.processing.Types.Lines

object Filters {

  val R1 = """:\s+(.*)""".r
  val R11 = """\s+(.*)""".r
  val R2 = """\[(\d+)\].*""".r
  val R3 = """\[(\d+\.\d)\].*""".r
  val R4 = """\[(\d+\.\d\d)\].*""".r
  val R5 = """\[(\d+\.\d\d\d)\].*""".r
  val R6 = """\[(\d+\.\d\d\d\d)\].*""".r
  val R7 = """\[(\d+\.\d\d\d\d\d)\].*""".r



  val tractatus:Lines=>Lines =(lines: Seq[String])=>
    lines.map(line=>line match{
      case R1(aline:String) => Seq[String]("\n",aline)
      case R11(aline) => Seq[String](aline)
      case R2(num)   => Seq[String](s"# ${num} #")
      case R3(num)   => Seq[String](s"## ${num} ##")
      case R4(num)   => Seq[String](s"### ${num} ###")
      case R5(num)   => Seq[String](s"#### ${num} ####")
      case R6(num)   => Seq[String](s"##### ${num} #####")
      case R7(num)   => Seq[String](s"###### ${num} ######")
      case _ => Seq[String](line)
    }).flatten


}