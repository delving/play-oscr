name := "play-oscr"

version := "1.0-SNAPSHOT"

resolvers += "Delving Releases Repository" at "http://nexus.delving.org/nexus/content/groups/public"

libraryDependencies += "eu.delving" % "basex-scala-client_2.10" % "0.6.1"

libraryDependencies += "commons-io" % "commons-io" % "2.1"

//libraryDependencies += "eu.delving" % "definitions" % "1.0"

play.Project.playScalaSettings