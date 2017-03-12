package com.example

import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult, Materializer}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

/**
  * Created by salonivithalani on 2/18/17.
  */
object FlowStream extends App{

  implicit val system: ActorSystem = ActorSystem("TestSystem", ConfigFactory.load())
  implicit val mat : Materializer = ActorMaterializer()

  val s: Source[Int, NotUsed] = Source(1 to 10)
  val factorial: Source[String, NotUsed] = s.map(_.toString)

  factorial.runWith(lineSink("factorials.txt"))


  def lineSink(filename : String) : Sink[String, Future[IOResult]] = {

    val someFlow: Flow[String, ByteString, NotUsed] = Flow[String].map(s => ByteString(s"$s\n"))
    val path: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get(filename))
    val result: Sink[String, Future[IOResult]] = someFlow.toMat(path)(Keep.right[NotUsed, Future[IOResult]])
    result
  }
}
