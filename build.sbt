name := "play-oscr"

version := "1.0-SNAPSHOT"

resolvers += "Delving Releases Repository" at "http://nexus.delving.org/nexus/content/groups/public"

libraryDependencies += "eu.delving" % "basex-scala-client_2.10" % "0.6.1"

libraryDependencies += "commons-io" % "commons-io" % "2.1"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

play.Project.playScalaSettings