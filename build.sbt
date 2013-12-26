name := "play-oscr"

version := "1.0-SNAPSHOT"



libraryDependencies ++= Seq(
//  jdbc,
//  anorm,
//  cache
)

val appDependencies = Seq(
  "eu.delving"                %% "basex-scala-client"              % "0.6.1"
)

play.Project.playScalaSettings
