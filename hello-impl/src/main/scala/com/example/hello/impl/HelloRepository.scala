package com.example.hello.impl

import org.apache.logging.log4j.scala.Logging
import slick.dbio.DBIOAction
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.Future

case class Greeting(id: Option[Int] = None, name:String, salutation:String) {
  def greet:String = s"${salutation} ${name}!"
}

trait Db {
  val profile: JdbcProfile
  // val config: DatabaseConfig[JdbcProfile]
  val db: JdbcProfile#Backend#Database // = config.db
}

trait GreetingTable { this: Db =>
  import profile.api._

  class Greetings(tag: Tag) extends Table[Greeting](tag, "GREETINGS") {
    // Columns
    def id = column[Int]("GREETING_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME", O.Length(512), O.Unique)
    def salutation = column[String]("SALUTATION", O.Length(64))

    // Indexes
    def nameIndex = index("NAME_IDX", name, true)

    // Select
    def * = (id.?, name, salutation) <> (Greeting.tupled, Greeting.unapply)
  }

  val greetings = TableQuery[Greetings]

}

class HelloRepository(val profile: JdbcProfile, val db: JdbcBackend#Database)
  extends Db with GreetingTable with Logging {

  import profile.api._

  def init() = DBIOAction.seq(greetings.schema.create)
  def drop() = DBIOAction.seq(greetings.schema.drop)

  def update(greeting:Greeting): slick.dbio.DBIOAction[Any, NoStream, Nothing] = {

    import scala.concurrent.ExecutionContext.Implicits.global

    for {
      n <- greetings.filter(g => g.name === greeting.name).map(_.salutation).update(greeting.salutation)
      result <- n match {
        case 0 => greetings += greeting
        case 1 => DBIO.successful(1)
        case n => DBIO.failed(new RuntimeException("wtf..."))
      }
    } yield result
  }

  def findGreetingForId(name:String):Future[Option[Greeting]] = {
    db.run((for (
      u <- greetings.filter(g => g.name === name)
    ) yield u).result.headOption)
  }

  def findGreetings = db.run(greetings.result)

}
