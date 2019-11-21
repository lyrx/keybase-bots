package com.lyrx.text.processing

import Types._

import scala.concurrent.{ExecutionContext, Future}

class Book(
           val context:  Context,
           val sections:Iterable[Section]
          ) extends Chunker {



}
