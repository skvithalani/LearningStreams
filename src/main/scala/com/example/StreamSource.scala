package com.example


import java.nio.file.{Path, Paths}

import akka.{Done, NotUsed}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult, Materializer}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object StreamSource extends App{

  val s1 = Source.empty

  val s2: Source[String, NotUsed] = Source.single("1 2 3")

  val s3: Source[Int, NotUsed] = Source(1 to 10)

  val s4 = Source.fromFuture(Future("This is from future"))

  val s5 = Source.repeat(5)

  val factorials: Source[String, NotUsed] = s3.scan("s")((acc, next) => acc + next)


  val stringFactorials: Source[ByteString, NotUsed] = factorials.map(num => ByteString(s"$num\n"))



  implicit val system: ActorSystem = ActorSystem("TestSystem", ConfigFactory.load())
  implicit val mat : Materializer = ActorMaterializer()
  val path: Path = Paths.get("factorials.txt")
  val file: Sink[ByteString, Future[IOResult]] = FileIO.toPath(path)
  val result: Future[IOResult] = stringFactorials.runWith(file)

  result.onComplete(println)
//  val f: Future[Done] = s2.runForeach(println)

//  f.onComplete(println)
//println(f)

  val actorRef : ActorRef = system.actorOf(Props[StreamActor](new StreamActor()))
//  actorRef ! factorials

}

class StreamActor() extends Actor {

  implicit val mate = ActorMaterializer()
  override def receive: Receive = {
    case s : Source[String, NotUsed] => s.runForeach(println)
  }
}
