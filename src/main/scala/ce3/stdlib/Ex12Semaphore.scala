package ce3.stdlib

import cats.effect._
import cats.effect.std._
import cats.effect.syntax.all._
import cats.implicits._
import scala.concurrent.duration._

object Ex12Semaphore extends IOApp.Simple {

  class PreciousResource[F[_]: Temporal](name: String, s: Semaphore[F])(implicit F: Console[F]) {
    def use: F[Unit] =
      for {
        x <- s.available
        _ <- F.println(s"$name >> Availability: $x")
        _ <- s.acquire
        y <- s.available
        _ <- F.println(s"$name >> Started | Availability: $y")
        _ <- s.release.delayBy(3.seconds)
        z <- s.available
        _ <- F.println(s"$name >> Done | Availability: $z")
      } yield ()
  }

  val run: IO[Unit] =
    for {
      _ <- IO.println("-----------------------------------------------------")
      s <- Semaphore[IO](1)
      r1 = new PreciousResource[IO]("R1", s)
      r2 = new PreciousResource[IO]("R2", s)
      r3 = new PreciousResource[IO]("R3", s)
      _ <- List(r1.use, r2.use, r3.use).parSequence.void
      _ <- IO.println("-----------------------------------------------------")
    } yield ()
}
