// see: https://typelevel.org/blog/2020/10/30/concurrency-in-ce3.html

package ce3.concurrency

import cats.effect.{IO, IOApp}
import cats.syntax.all._

object ExampleOne extends IOApp.Simple {

  def repeat(letter: String): IO[Unit] =
    IO.print(letter).replicateA(100).void

  override def run: IO[Unit] =
    for {
      fa <- (repeat("A") *> repeat("B")).as("foo!").start
      fb <- (repeat("C") *> repeat("D")).as("bar!").start
      // joinWithNever is a variant of join that asserts
      // the fiber has an outcome of Succeeded and returns the
      // associated value.
      ra <- fa.joinWithNever
      rb <- fb.joinWithNever
      _  <- IO.println(s"\ndone: a says: $ra, b says: $rb")
    } yield ()
}
