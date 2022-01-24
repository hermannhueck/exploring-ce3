// see: https://typelevel.org/blog/2020/10/30/concurrency-in-ce3.html

package ce3.concurrency

import cats.effect.{IO, IOApp}
import cats.effect.kernel.{Deferred, Ref}
import cats.syntax.all._
import scala.concurrent.duration._

sealed trait State
final case class Awaiting(latches: Int, waiter: Deferred[IO, Unit]) extends State
case object Done                                                    extends State

trait Latch  {
  def release: IO[Unit]
  def await: IO[Unit]
}
object Latch {
  def apply(latches: Int): IO[Latch] =
    for {
      waiter <- IO.deferred[Unit]
      state  <- IO.ref[State](Awaiting(latches, waiter))
    } yield new Latch {
      override def release: IO[Unit] =
        state
          .modify {
            case Awaiting(n, waiter) =>
              if (n > 1)
                (Awaiting(n - 1, waiter), IO.unit)
              else
                (Done, waiter.complete(()))
            case Done                => (Done, IO.unit)
          }
          .flatten
          .void
      override def await: IO[Unit]   =
        state.get.flatMap {
          case Done                => IO.unit
          case Awaiting(_, waiter) => waiter.get
        }
    }
}

object ExampleSix extends IOApp.Simple {

  override def run: IO[Unit] =
    for {
      latch <- Latch(10)
      _     <- (1 to 10).toList.traverse { idx =>
                 (IO.println(s"$idx counting down") *> latch.release).start
               }
      _     <- latch.await
      _     <- IO.println("Got past the latch")
    } yield ()
}
