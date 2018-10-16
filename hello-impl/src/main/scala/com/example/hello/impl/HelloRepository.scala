package com.example.hello.impl

import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import slick.dbio.DBIOAction
import slick.jdbc.{JdbcBackend, JdbcProfile}

case class Greeting(id: Option[Int] = None, name:String, salutation:String) {
  def greet():String = {
      s"${salutation} ${name}"
  }
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

class HelloRepository(val profile: JdbcProfile, val db: JdbcBackend#Database, val slickReadSide: SlickReadSide)
  extends Db with GreetingTable {

  import profile.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  // ...
  def init() = DBIOAction.seq(greetings.schema.create)
  def drop() = DBIOAction.seq(greetings.schema.drop)

  def update(greeting:Greeting) = {
    greetings returning greetings.map(_.id) += greeting.map(id => greeting.copy(id = Some(id)))
  }

  def findGreetingForId(id:String) =
    db.run((for (u <- greetings if u.id == id) yield u).result.headOption)

  def findGreetings = db.run(greetings.result)

}
