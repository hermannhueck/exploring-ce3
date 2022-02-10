package ce3.stdlib

import cats.implicits._
import cats.effect._
import cats.effect.std.CountDownLatch

object Ex01CountdownLatch extends IOApp.Simple {

  val run: IO[Unit] =
    for {
      _ <- IO.println("-----------------------------------------------------")
      c <- CountDownLatch[IO](2)
      f <- (c.await >> IO.println(s"${Thread.currentThread().getName()}: Countdown latch unblocked")).start
      _ <- c.release
      _ <- IO.println(s"${Thread.currentThread().getName()}: Before latch is unblocked")
      _ <- c.release
      _ <- f.join
      _ <- IO.println("-----------------------------------------------------")
    } yield ()
}
