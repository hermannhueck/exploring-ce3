// https://typelevel.org/cats-effect/docs/core/test-runtime

package ce3.test_runtime

import cats.instances.list._
import cats.syntax.traverse._
import cats.effect.IO
import cats.effect.std.Random
import cats.effect.kernel.Outcome
import cats.effect.testkit.TestControl
import scala.concurrent.duration._
import munit.CatsEffectSuite

class RetryTestSuite extends CatsEffectSuite {

  import Retry.retry

  case object TestException extends RuntimeException

  test("retry at least 3 times until success") {

    var attempts = 0
    val action   = IO {
      attempts += 1
      if (attempts != 3) throw TestException else "success!"
    }

    val program = Random.scalaUtilRandom[IO] flatMap { random =>
      retry(action, 1.minute, 5, random)
    }

    TestControl.executeEmbed(program).assertEquals("success!")
  }

  test("backoff appropriately between attempts") {

    val action  = IO.raiseError[Int](TestException)
    val program = Random.scalaUtilRandom[IO] flatMap { random =>
      retry(action, 1.minute, 5, random)
    }

    TestControl.execute(program) flatMap { control =>
      for {
        _ <- control.results.assertEquals(None)
        _ <- control.tick

        _ <- 0.until(4).toList traverse { i =>
               for {
                 _ <- control.results.assertEquals(None)

                 interval <- control.nextInterval
                 _        <- IO(assert(interval >= 0.nanos))
                 _        <- IO(assert(interval < (1 << i).minute))
                 _        <- control.advanceAndTick(interval)
               } yield ()
             }

        _ <- control.results.assertEquals(Some(Outcome.errored[cats.Id, Throwable, Int](TestException)))
      } yield ()
    }
  }
}
