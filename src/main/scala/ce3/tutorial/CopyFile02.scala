/*
IO-kungfu - Exercise 01:
  
Modify the IOApp so it shows an error and abort the execution if the origin and destination files are the same,
the origin file cannot be open for reading or the destination file cannot be opened for writing.
Also, if the destination file already exists, the program should ask for confirmation before overwriting that file.
*/

package ce3.tutorial

import cats.syntax.all._
import cats.effect._
import cats.effect.std.Console
import java.io._


object CopyFile02 extends IOApp {

  def inputStream[F[_]: Sync](f: File): Resource[F, FileInputStream] =
    Resource.make {
      Sync[F].blocking(new FileInputStream(f))                         // build
    } { inStream =>
      Sync[F].blocking(inStream.close()).handleErrorWith(_ => Sync[F].unit) // release
    }

  def outputStream[F[_]: Sync](f: File): Resource[F, FileOutputStream] =
    Resource.make {
      Sync[F].blocking(new FileOutputStream(f))                         // build 
    } { outStream =>
      Sync[F].blocking(outStream.close()).handleErrorWith(_ => Sync[F].unit) // release
    }

  def inputOutputStreams[F[_]: Sync](in: File, out: File): Resource[F, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

  def transmit[F[_]: Sync](origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): F[Long] =
    for {
      amount <- Sync[F].blocking(origin.read(buffer, 0, buffer.size))
      count  <- if ( amount > -1 )
                  Sync[F].blocking(destination.write(buffer, 0, amount)) >>
                    transmit(origin, destination, buffer, acc + amount)
                else
                  Sync[F].pure(acc) // End of read stream reached (by java.io.InputStream contract), nothing to write
    } yield count // Returns the actual amount of bytes transmitted // Returns the actual amount of bytes transmitted

  def transfer[F[_]: Sync](origin: InputStream, destination: OutputStream): F[Long] =
    transmit(origin, destination, new Array[Byte](1024 * 10), 0L)

  def copy[F[_]: Sync](origin: File, destination: File): F[Long] = 
    inputOutputStreams(origin, destination).use { case (in, out) => 
      transfer(in, out)
    }

  def sameFiles[F[_]: Sync](file1: File, file2: File): F[Boolean] =
    Sync[F].blocking(file1.getAbsolutePath() == file2.getAbsolutePath())

  def askUser[F[_]: Console: Sync](question: String): F[Boolean] =
    for {
      _ <- Console[F].println(question)
      answer <- Console[F].readLine
    } yield answer.toLowerCase.startsWith("y")

  implicit val console = Console.apply[IO]

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _      <- if (args.length < 2) IO.raiseError(new IllegalArgumentException("Need origin and destination files"))
                else IO.unit
      orig = new File(args(0))
      dest = new File(args(1))
      sameFiles <- sameFiles[IO](orig, dest)
      _ <- if (sameFiles) IO.raiseError(new IllegalArgumentException("Origin and destination files are the same"))
          else IO.unit
      _ <- if (!orig.exists()) IO.raiseError(new IllegalArgumentException("Source file doesn't exist"))
          else IO.unit
      _ <- if (!orig.canRead()) IO.raiseError(new IllegalArgumentException("Source file cannot be read"))
          else IO.unit
      // doCopy <- if (dest.exists()) askUser("Destination file exists, overwrite? (y/n) ")
      //     else IO.pure(true)
      // _ <- if (!doCopy) IO.raiseError(new IllegalArgumentException("Will not overwrite destination file"))
      //     else IO.unit
      count <- copy[IO](orig, dest)
      _     <- IO.println(s"$count bytes copied from ${orig.getPath} to ${dest.getPath}")
    } yield ExitCode.Success
}
