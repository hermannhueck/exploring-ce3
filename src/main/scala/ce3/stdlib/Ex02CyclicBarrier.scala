package ce3.stdlib

import cats.implicits._
import cats.effect._
import cats.effect.std.CyclicBarrier
import scala.concurrent.duration._

object Ex02CyclicBarrier extends IOApp.Simple {

  val run: IO[Unit] =
    for {
      b <- CyclicBarrier[IO](2)
      f1 <- (IO.println("fast fiber before barrier") >>
          b.await >> 
          IO.println("fast fiber after barrier")
        ).start
      f2 <- (IO.sleep(1.second) >>
          IO.println("slow fiber before barrier") >>
          IO.sleep(1.second) >>
          b.await >>
          IO.println("slow fiber after barrier")
        ).start
      _ <- (f1.join, f2.join).tupled
    } yield ()

}
