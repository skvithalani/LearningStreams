package com.example

import javax.jmdns.{JmDNS, ServiceEvent, ServiceListener}

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, SinkQueueWithCancel, Source, SourceQueueWithComplete}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

class JmdnsListener{
  implicit val actorSystem = ActorSystem("as")
  implicit val mat = ActorMaterializer()

  val jmdns = JmDNS.create()
  new Thread(new inner).start()
  val source: Source[ConnectionState, SourceQueueWithComplete[ConnectionState]] = Source.queue[ConnectionState](10, OverflowStrategy.fail)
  val sink: Sink[ConnectionState, SinkQueueWithCancel[ConnectionState]] = Sink.queue[ConnectionState]()

  val graph: SourceQueueWithComplete[ConnectionState] = source.to(sink).run()

  val (source1, futureQueue) = convert(source)

  def convert (source : Source[ConnectionState, SourceQueueWithComplete[ConnectionState]]) = {
    val promise : Promise[SourceQueueWithComplete[ConnectionState]] = Promise()
    val s = source.mapMaterializedValue(queue => promise.trySuccess(queue))
    (s , promise.future)
  }

  val jmDNSServiceListener = new ServiceListener {

    override def serviceAdded(event: ServiceEvent) = {
//      futureQueue.foreach(queue => queue offer ConnectionAdded(event))
      graph.offer(ConnectionAdded(event))
      println("Added")
    }

    override def serviceResolved(event: ServiceEvent) = {
      futureQueue.foreach(queue => queue offer ConnectionResolved(event))
      println("--resolved")
    }

    override def serviceRemoved(event: ServiceEvent) = {
      futureQueue.foreach(queue => queue offer ConnectionRemoved(event))
    }
  }

  def getSource = source1

  jmdns.addServiceListener("_csw._tcp.local.", jmDNSServiceListener)

  class inner extends Runnable {
    override def run(): Unit = {
      while (true) {
      }
    }
  }

}

object JmdnsListener extends App {
  val listener = new JmdnsListener()
}
