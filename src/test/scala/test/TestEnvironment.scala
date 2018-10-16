package test

import java.net.URI

import automat.{Environment, Resource}

object TestEnvironments {
  val LOCAL = TestEnvironment("127.0.0.1", 9000)
}

case class TestEnvironment(host: String, port: Int) extends Environment {
  override def toUri(resource: Resource): URI = toUri("http", resource)

  override def toUri(protocol: String, resource: Resource): URI = {
    new URI(protocol, null, host, port, resource.uri(), null, null)
  }
}
