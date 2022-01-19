import sbt._
import Versions._

object Dependencies {

  lazy val libraries =
    Seq(
      // "core" module - IO, IOApp, schedulers
      // This pulls in the kernel and std modules automatically.
      "org.typelevel" %% "cats-effect"   % catsEffectVersion,
      // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
      // "org.typelevel" %% "cats-effect-kernel"   % catsEffectVersion,
      // standard "effect" library (Queues, Console, Random etc.)
      // "org.typelevel" %% "cats-effect-std"   % catsEffectVersion,
      "org.typelevel" %% "cats-effect-testkit"   % catsEffectVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % munitCe3Version % Test,
      "com.disneystreaming" %% "weaver-cats" % weaverCatsVersion % Test
    )

  lazy val compilerPlugins =
    Seq(
      compilerPlugin("org.typelevel" % "kind-projector"     % kindProjectorVersion cross CrossVersion.full),
      compilerPlugin("com.olegpy"   %% "better-monadic-for" % betterMonadicForVersion)
    )

  lazy val dependencies = libraries ++ compilerPlugins
}
