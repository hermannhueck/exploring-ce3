package ce3.getting_started

import cats.effect.{IO, SyncIO}
import munit.CatsEffectSuite

class MUnitExampleSuite extends CatsEffectSuite {
  test("make sure IO computes the right result") {
    IO.pure(1).map(_ + 2) flatMap { result =>
      IO(assertEquals(result, 3))
    }
  }
}
