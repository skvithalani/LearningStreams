package com.example

import javax.jmdns.{JmDNS, ServiceInfo}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, SinkQueueWithCancel, Source}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success
object jmdns extends App{

  val jmdns = JmDNS.create()

  val service = ServiceInfo.create("_csw._tcp.local.", "config_service", 8080, "")
  jmdns.registerService(service)

  implicit val system = ActorSystem("system")
  implicit val materializer = ActorMaterializer()

  val sink: Sink[ConnectionState, SinkQueueWithCancel[ConnectionState]] = Sink.queue[ConnectionState]()

  val source: Source[ConnectionState, Boolean] = JmdnsListener.listener.source1

  val graph: RunnableGraph[SinkQueueWithCancel[ConnectionState]] = source.toMat(sink)(Keep.right)

  val sinkQueue: SinkQueueWithCancel[ConnectionState] = graph.run()
  val future: Future[Option[ConnectionState]] = sinkQueue.pull()
  sinkQueue.pull()

  println(future)
  future.onComplete(x => x match {
    case Success(Some(y : ConnectionAdded)) => println(y.event)
  })

}

