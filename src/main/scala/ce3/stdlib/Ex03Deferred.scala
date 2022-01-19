package ce3.stdlib

import cats.effect.{Deferred, IO, IOApp}
import cats.syntax.all._

object Ex03Deferred extends IOApp.Simple {

  def start(d: Deferred[IO, Int]): IO[Unit] = {

    val attemptCompletion: Int => IO[Unit] =
      n => d.complete(n).attempt.void

    List(
      IO.race(attemptCompletion(1), attemptCompletion(2)),
      d.get.flatMap { n => IO(println(show"Result: $n")) }
    ).parSequence.void
  }

  val run: IO[Unit] =
    for {
      d <- Deferred[IO, Int]
      _ <- start(d)
    } yield ()
}
