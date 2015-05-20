name := "run-vsd"

version := "1.0"

scalaVersion := "2.11.6"

mainClass in (Compile, run) := Some("VnsRunner")

libraryDependencies ++= Seq(
  "de.bwaldvogel" % "liblinear" % "1.94",
  "com.clearnlp" % "clearnlp-dictionary" % "1.0",
  "com.clearnlp" % "clearnlp-general-en-pos" % "1.1",
  "com.clearnlp" % "clearnlp-general-en-dep" % "1.2",
  "com.clearnlp" % "clearnlp-general-en-srl" % "1.1",
  "com.clearnlp" % "clearnlp" % "2.0.2",
  "commons-io" % "commons-io" % "2.4",
  "edu.mit" % "jwi" % "2.2.3",
  "org.apache.lucene" % "lucene-core" % "3.6.2",
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3"
)
