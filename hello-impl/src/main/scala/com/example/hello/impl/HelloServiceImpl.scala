package com.example.hello.impl

import com.example.hello.api
import com.example.hello.api.HelloService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

import scala.concurrent.Future

/**
  * Implementation of the HelloService.
  */
class HelloServiceImpl(repository:HelloRepository, persistentEntityRegistry: PersistentEntityRegistry) extends HelloService {

  override def hello(id: String) = ServiceCall { _ =>

    import scala.concurrent.ExecutionContext.Implicits.global

    val greeting:Future[Option[Greeting]] = repository.findGreetingForId(id)
    greeting.map{
      case Some(g) => g.greet
      case None => "default greeting"
    }
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the Hello entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HelloEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(id,request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(HelloEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[HelloEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingChangedEvent(name,msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
