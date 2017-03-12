package com.example

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GraphStream extends App{

  implicit val system = ActorSystem("ActorSystem")
  implicit val mat = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 10)

  val flow: Flow[Int, Int, NotUsed] = Flow[Int].map[Int](s => s * 2)

  val source1: Source[Int, NotUsed] = source.via(flow)

  val sink : Sink[Int, Future[Double]] = Sink.fold(1.0)((acc, value) => acc + value)

  val runnableGraph: RunnableGraph[Future[Double]] = source1.toMat(sink)(Keep.right)

  val result: Future[Double] = runnableGraph.run()
  val result1: Future[Double] = runnableGraph.run()

  println(result)
  println(result1)

  result.onComplete(s => println(s))
  result1.onComplete(s => println(s))

  system.terminate()

}
