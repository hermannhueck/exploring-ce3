package ce3.stdlib

import cats.effect._
import cats.syntax.all._
import java.io._

object Ex11Resource extends IOApp {

  def openFileR(path: String): IO[FileInputStream] =
    IO.blocking(new FileInputStream(path))

  def resourceFileR(path: String): Resource[IO, FileInputStream] =
    Resource.make(openFileR(path))(in => IO.blocking(in.close))

  def openFileW(path: String): IO[FileOutputStream] =
    IO.blocking(new FileOutputStream(path))

  def resourceFileW(path: String): Resource[IO, FileOutputStream] =
    Resource.make(openFileW(path))(out => IO.blocking(out.close))

  def concat(f1: String, f2: String, f3: String): IO[Unit] =
    (
      for {
        in1 <- resourceFileR(f1)
        in2 <- resourceFileR(f2)
        out <- resourceFileW(f3)
      } yield (in1, in2, out)
    ).use { case (in1, in2, out) =>
      IO.blocking(out.write(in1.readAllBytes ++ in2.readAllBytes))
    }

  def run(args: List[String]): IO[ExitCode] = for {
    _        <- IO.println("-----------------------------------------------------")
    exitCode <- if (args.length != 3)
                  IO(println("Usage: Ex11Resource <file1> <file2> <file3>")) *> IO.pure(ExitCode.Error)
                else
                  concat(args(0), args(1), args(2)).as(ExitCode.Success)
    _        <- IO.println("-----------------------------------------------------")
  } yield exitCode
}
