package com.example.hello.impl

import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import slick.dbio.{DBIOAction, NoStream}

class HelloEventProcessor(
                           readSide: SlickReadSide,
                           repo: HelloRepository

                         ) extends ReadSideProcessor[HelloEvent] {

  override def buildHandler(): ReadSideHandler[HelloEvent] = {
    readSide.builder[HelloEvent]("helloEventOffset")
      .setPrepare { tag =>
        prepareStatements()
      }.setEventHandler[GreetingMessageChanged](updateGreeting)
      .build()
  }

  override def aggregateTags: Set[AggregateEventTag[HelloEvent]] = {
    HelloEvent.Tag.allTags
  }

  def prepareStatements(): DBIOAction[Any, NoStream, Nothing] = {
    repo.init()
  }

  def updateGreeting: EventStreamElement[GreetingMessageChanged] => DBIOAction[Any, NoStream, Nothing] = {
    e => repo.findGreetingForId(e.event.)
  }

}

