organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

lagomCassandraEnabled in ThisBuild := false

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val h2db = "com.h2database" % "h2" % "1.4.196"
val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
val log4j2_api = "org.apache.logging.log4j" % "log4j-api" % "2.11.0"
val log4j2_core = "org.apache.logging.log4j" % "log4j-core" % "2.11.0"
val log4j2_api_scala =  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"
val automat = "automat" %% "automat" % "0.1"
val slick = "com.typesafe.slick" %% "slick" % "3.2.3"
val akkaJdbc = "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.0.0-7671235"

lazy val `hello` = (project in file("."))
  .aggregate(`hello-api`, `hello-impl`)
  .settings(
    libraryDependencies ++= Seq(
      scalatest,
      log4j2_api,
      log4j2_core,
      log4j2_api_scala,
      automat
    )
  )

lazy val `hello-api` = (project in file("hello-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )



lazy val `hello-impl` = (project in file("hello-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceJdbc,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      slick,
      macwire,
      h2db,
      akkaJdbc,
      log4j2_api,
      log4j2_core,
      log4j2_api_scala,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`hello-api`)
