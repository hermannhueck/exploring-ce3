// see: https://typelevel.org/blog/2020/10/30/concurrency-in-ce3.html

package ce3.concurrency

import cats.effect.{IO, IOApp}
import cats.syntax.all._

object ExampleFour extends IOApp.Simple {

  override def run: IO[Unit] =
    for {
      state  <- IO.ref(0)
      fibers <- state.update(_ + 1).start.replicateA(100)
      _      <- fibers.traverse(_.join).void
      value  <- state.get
      _      <- IO.println(s"The final value is: $value")
    } yield ()
}
