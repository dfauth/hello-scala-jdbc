package com.example.hello.impl

import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import org.apache.logging.log4j.scala.Logging
import slick.dbio.{DBIOAction, NoStream}

class HelloEventProcessor(
                           readSide: SlickReadSide,
                           repo: HelloRepository

                         ) extends ReadSideProcessor[HelloEvent] with Logging {

  override def buildHandler(): ReadSideHandler[HelloEvent] = {
    readSide.builder[HelloEvent]("helloEventOffset")
      .setPrepare { tag =>
        prepareStatements()
      }.setEventHandler[GreetingChangedEvent](updateGreeting)
      .build()
  }

  override def aggregateTags: Set[AggregateEventTag[HelloEvent]] = {
    Set(HelloEvent.Tag)
  }

  def prepareStatements(): DBIOAction[Any, NoStream, Nothing] = {
    repo.drop()
    repo.init()
  }

  def updateGreeting: EventStreamElement[GreetingChangedEvent] => DBIOAction[Any, NoStream, Nothing] = {
    e => {
      logger.info(s"received event: ${e} updating db")
      repo.update(Greeting(name= e.event.name, salutation = e.event.message))
    }
  }

}

