// see: https://typelevel.org/cats-effect/docs/core/test-runtime

package ce3.test_runtime

import cats.effect.IO
import cats.effect.std.Random
import scala.concurrent.duration._

object Retry {

  def retry[A](ioa: IO[A], delay: FiniteDuration, max: Int, random: Random[IO]): IO[A] =
    if (max <= 1)
      ioa
    else
      ioa handleErrorWith { _ =>
        random.betweenLong(0L, delay.toNanos) flatMap { ns =>
          IO.sleep(ns.nanos) *> retry(ioa, delay * 2, max - 1, random)
        }
      }
}
