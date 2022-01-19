package ce3.typeclasses

import cats.effect.IO
import cats.effect.unsafe.implicits.global

object tc05Concurrent extends App {

  val action: IO[String] = IO.println("This is only printed once").as("action")

  val x: IO[String] = for {
    memoized <- action.memoize
    res1     <- memoized
    res2     <- memoized
  } yield res1 ++ res2

  val res = x.unsafeRunSync()
  // res0: String = "actionaction"
  println(res)
}
