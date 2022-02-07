import sbtassembly.AssemblyPlugin.assemblySettings

lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion    = "2.6.18"

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "edu.ucr.cs.bdlab",
      scalaVersion    := "2.13.4"
    )),
    name := "beast-backend-api",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.2.10",
      "com.typesafe.slick" %% "slick" % "3.3.3",
      "com.h2database" % "h2" % "2.1.210",
      "org.apache.spark" %% "spark-core" % "3.2.0" % "provided",
      "org.apache.spark" %% "spark-sql" % "3.2.0" % "provided",
//      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
//      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
//      "org.scalatest"     %% "scalatest"                % "3.2.9"         % Test
    )
  )


// MERGE STRATEGY FOR SBT ASSEMBLY UBER JAR
assemblySettings
//assembly / assemblyMergeStrategy := {
//  case manifest if manifest.contains("MANIFEST.MF") =>
//    // We don't need manifest files since sbt-assembly will create
//    // one with the given settings
//    MergeStrategy.discard
//  case reference if reference.contains("reference.conf") =>
//    MergeStrategy.concat
//  case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
//    // Keep the content for all reference-overrides.conf files
//    MergeStrategy.concat
//  case moduleInfo if moduleInfo.contains("module-info.class") =>
//    // Keep the content for all module-info.class files
//    MergeStrategy.discard
//  case _ =>
//    // For all the other files, use the default sbt-assembly merge strategy
//    MergeStrategy.discard
  //
  //    val oldStrategy = (assembly / assemblyMergeStrategy).value
//    oldStrategy(x)
//}


