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

}


