package test

import automat.Resource

object TestResources {

  val HELLO = TestResource(s"/api/hello/{0}")

}

case class TestResource(resource: String) extends Resource {

  override def uri(): String = resource
}
