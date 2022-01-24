// see: https://typelevel.org/blog/2020/10/30/concurrency-in-ce3.html

package ce3.concurrency

import cats.effect.{IO, IOApp}
import cats.syntax.all._
import scala.concurrent.duration._

object ExampleTwo extends IOApp.Simple {

  override def run: IO[Unit] =
    for {
      fiber <- IO.println("hello!").foreverM.start
      _     <- IO.sleep(5.seconds)
      _     <- fiber.cancel
    } yield ()
}
