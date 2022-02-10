package ce3.stdlib

import cats.effect.{ExitCode, IO, IOApp, Ref, Sync}
import cats.syntax.all._
import cats.effect.ExitCode
import java.io._

object Ex11Bracket extends IOApp {

  def openFileR(path: String): IO[FileInputStream] =
    IO.blocking(new FileInputStream(path))

  def openFileW(path: String): IO[FileOutputStream] =
    IO.blocking(new FileOutputStream(path))

  def concat(f1: String, f2: String, f3: String): IO[Unit] =
    (openFileR(f1)).bracket { file1 =>
      (openFileR(f2)).bracket { file2 =>
        (openFileW(f3)).bracket { file3 =>
          IO.blocking(file3.write(file1.readAllBytes ++ file2.readAllBytes))
        }(file3 => IO.blocking(file3.close()))
      }(file2 => IO.blocking(file2.close()))
    }(file1 => IO.blocking(file1.close()))

  def run(args: List[String]): IO[ExitCode] = for {
    _        <- IO.println("-----------------------------------------------------")
    exitCode <- if (args.length != 3)
                  IO(println("Usage: Ex11Bracket <file1> <file2> <file3>")) *> IO.pure(ExitCode.Error)
                else
                  concat(args(0), args(1), args(2)).as(ExitCode.Success)
    _        <- IO.println("-----------------------------------------------------")
  } yield exitCode
}
