/*
 * Copyright 2019 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//=============================================================================
//============================== build details ================================
//=============================================================================

addCommandAlias("format", ";scalafmtSbt;scalafmtConfig;scalafmtAll")
addCommandAlias("github-gen", "githubWorkflowGenerate")
addCommandAlias("github-check", "githubWorkflowCheck")

Global / onChangedBuildSource := ReloadOnSourceChanges

val Scala213 = "2.13.5"

//=============================================================================
//============================ publishing details =============================
//=============================================================================

//see: https://github.com/xerial/sbt-sonatype#buildsbt
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

ThisBuild / baseVersion      := "0.5"
ThisBuild / organization     := "com.busymachines"
ThisBuild / organizationName := "BusyMachines"
ThisBuild / homepage         := Option(url("https://github.com/busymachines/pureharm-rest"))

ThisBuild / scmInfo := Option(
  ScmInfo(
    browseUrl  = url("https://github.com/busymachines/pureharm-testkit"),
    connection = "git@github.com:busymachines/pureharm-testkit.git",
  )
)

/** I want my email. So I put this here. To reduce a few lines of code,
  * the sbt-spiewak plugin generates this (except email) from these two settings:
  * {{{
  * ThisBuild / publishFullName   := "Loránd Szakács"
  * ThisBuild / publishGithubUser := "lorandszakacs"
  * }}}
  */
ThisBuild / developers := List(
  Developer(
    id    = "lorandszakacs",
    name  = "Loránd Szakács",
    email = "lorand.szakacs@protonmail.com",
    url   = new java.net.URL("https://github.com/lorandszakacs"),
  )
)

ThisBuild / startYear  := Some(2021)
ThisBuild / licenses   := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

//until we get to 1.0.0, we keep strictSemVer false
ThisBuild / strictSemVer              := false
ThisBuild / spiewakCiReleaseSnapshots := true
ThisBuild / spiewakMainBranches       := List("main")
ThisBuild / Test / publishArtifact    := false

ThisBuild / scalaVersion       := Scala213
ThisBuild / crossScalaVersions := List(Scala213)

//required for binary compat checks
ThisBuild / versionIntroduced := Map(
  Scala213 -> "0.1.0"
)

//=============================================================================
//================================ Dependencies ===============================
//=============================================================================
ThisBuild / resolvers += Resolver.sonatypeRepo("releases")
ThisBuild / resolvers += Resolver.sonatypeRepo("snapshots")

// format: off
val pureharmCoreV        = "0.2.0"      //https://github.com/busymachines/pureharm-core/releases
val pureharmEffectsV     = "0.4.0"      //https://github.com/busymachines/pureharm-effects-cats/releases
val pureharmJSONV        = "0.2.0"      //https://github.com/busymachines/pureharm-json-circe/releases
val pureharmTestkitV     = "0.3.0"      //https://github.com/busymachines/pureharm-testkit/releases
val http4sV              = "0.21.22"    //https://github.com/http4s/http4s/releases
val tapirV               = "0.17.19"    //https://github.com/softwaremill/tapir/releases
val log4catsV            = "1.2.2"      //https://github.com/typelevel/log4cats/releases
val logbackV             = "1.2.3"      //https://github.com/qos-ch/logback/releases
// format: on
//=============================================================================
//============================== Project details ==============================
//=============================================================================

lazy val root = project
  .in(file("."))
  .aggregate(
    `endpoint-tapir`,
    `endpoint-docs-tapir`,
    `route-http4s`,
    `testing`,
  )
  .enablePlugins(NoPublishPlugin)
  .enablePlugins(SonatypeCiReleasePlugin)
  .settings(commonSettings)

lazy val `endpoint-tapir` = project
  .settings(commonSettings)
  .settings(
    name := "pureharm-endpoint-tapir",
    libraryDependencies ++= Seq(
      // format: off
      "com.busymachines"              %% "pureharm-core-anomaly"      % pureharmCoreV       withSources(),
      "com.busymachines"              %% "pureharm-core-sprout"       % pureharmCoreV       withSources(),
      "com.busymachines"              %% "pureharm-effects-cats"      % pureharmEffectsV    withSources(),
      "com.busymachines"              %% "pureharm-json-circe"        % pureharmJSONV       withSources(),
      "com.softwaremill.sttp.tapir"   %% "tapir-core"                 % tapirV              withSources(),
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"           % tapirV              withSources(),
      // format: on
    ),
  )

lazy val `endpoint-docs-tapir` = project
  .settings(commonSettings)
  .settings(
    name := "pureharm-endpoints-tapir-docs",
    libraryDependencies ++= Seq(
      // format: off
      "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"          % tapirV       withSources(),
      "com.softwaremill.sttp.tapir"   %% "tapir-openapi-circe-yaml"    % tapirV       withSources(),
      // format: on
    ),
  )

lazy val `route-http4s` = project
  .settings(commonSettings)
  .settings(
    name := "pureharm-route-http4s-tapir",
    libraryDependencies ++= Seq(
      // format: off
      "org.http4s"                    %% "http4s-dsl"                 % http4sV             withSources(),
      "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"        % tapirV              withSources(),
      // format: on
    ),
  )
  .dependsOn(
    `endpoint-tapir`
  )

lazy val `server-http4s` = project
  .settings(commonSettings)
  .settings(
    name := "pureharm-server-http4s",
    libraryDependencies ++= Seq(
      // format: off
      "com.busymachines"              %% "pureharm-effects-cats"      % pureharmEffectsV    withSources(),
      "org.http4s"                    %% "http4s-blaze-server"        % http4sV             withSources(),
      // format: on
    ),
  )

lazy val `testing` = project
  .enablePlugins(NoPublishPlugin)
  .settings(commonSettings)
  .settings(
    name := "pureharm-rest-testing",
    libraryDependencies ++= Seq(
      // format: off
      "org.typelevel"         %% "log4cats-slf4j"         % log4catsV     withSources(),
      "ch.qos.logback"         % "logback-classic"        % logbackV      withSources()
      // format: on
    ),
  )
  .dependsOn(
    `endpoint-tapir`,
    `endpoint-docs-tapir`,
    `route-http4s`,
    `server-http4s`,
  )
//=============================================================================
//================================= Settings ==================================
//=============================================================================

lazy val commonSettings = Seq(
  Compile / unmanagedSourceDirectories ++= {
    val major = if (isDotty.value) "-3" else "-2"
    List(CrossType.Pure, CrossType.Full).flatMap(
      _.sharedSrcDir(baseDirectory.value, "main").toList.map(f => file(f.getPath + major))
    )
  },
  Test / unmanagedSourceDirectories ++= {
    val major = if (isDotty.value) "-3" else "-2"
    List(CrossType.Pure, CrossType.Full).flatMap(
      _.sharedSrcDir(baseDirectory.value, "test").toList.map(f => file(f.getPath + major))
    )
  },
)
