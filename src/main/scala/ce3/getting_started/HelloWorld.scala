package ce3.getting_started

import cats.effect.{IO, IOApp}

object HelloWorld extends IOApp.Simple {
  val run = IO.println("Hello, World!")
}
