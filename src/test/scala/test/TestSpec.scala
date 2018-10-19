package test


import java.util.Collections

import automat.Automat.given
import org.apache.logging.log4j.scala.Logging
import org.hamcrest.Matchers.is
import org.scalatest.{FlatSpec, Matchers}
import test.TestResources.HELLO
import test.TestEnvironments.LOCAL

class TestSpec extends FlatSpec with Matchers with Logging {

  "calling hello" should "work" in {

    given.environment(LOCAL).get(HELLO, "fred").then.log.body(true).statusCode(is[Integer](200))
  }

  "posting hello " should "work" in {

    given.environment(LOCAL).post(HELLO.apply("fred"), Collections.singletonMap("message", "bonjour")).then.log.body(true).statusCode(is[Integer](200))
  }

  "assert that updating the salutation " should "be persisted" in {

    val name:String = "fred"
    val salutation:String = "bonjour"

    given.environment(LOCAL).get(HELLO, name).then().body(is("Hello "+name+"!")).statusCode(is[Integer](200))
    given.environment(LOCAL).post(HELLO.apply(name), Collections.singletonMap("message", salutation)).then().statusCode(is[Integer](200))
    Thread.sleep(5000)
    given.environment(LOCAL).get(HELLO, name).then().body(is(salutation+" "+name+"!")).statusCode(is[Integer](200))
  }

}


