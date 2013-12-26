name := "play-oscr"

version := "1.0-SNAPSHOT"

resolvers += "Delving Releases Repository" at "http://development.delving.org:8081/nexus/content/groups/public"

libraryDependencies += "eu.delving" % "basex-scala-client" % "0.6.1"

val appDependencies = Seq()

play.Project(name.toString, version.toString, appDependencies).settings