package understandingstreams.ticking_producer_consumer

import akka.actor.ActorSystem
import akka.stream.scaladsl.{BroadcastHub, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import understandingstreams.common.SourceExtensions.RichSource

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationDouble

case class TickingProducer() {
  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()
  private val tickingSource = Source.tick(1.seconds, 2.seconds, s"A ticking message every 2 seconds")

  private val normalSource = Source(1 to 10)

  private val source = Source.queue[String](256, OverflowStrategy.dropHead)
  private val (sourceQ, queueF) = source.splitMat

  private val broadcastHub = tickingSource.toMat(BroadcastHub.sink)(Keep.right).run()
  private val (queue, source1)  = source.toMat(BroadcastHub.sink)(Keep.both).run

  queueF.onComplete(s => println("In future complete"))
  queueF.foreach(queue => {
    queue.offer("-1")
    queue.offer("-2")
    queue.offer("-3")
    queue.offer("-4")
    queue.offer("-5")
  })

  def getTickingSource = tickingSource

  def getBroadcastSource = broadcastHub

  def counter (initVal : Int) = {
    initVal + 1
  }

  def tryCallback(f : String => Unit) = {

    val queue = source.to(Sink.foreach(s => f(s))).run
    queue.offer("-1")
    queue.offer("2")
    queue.offer("-3")
    queue.offer("4")
    queue.offer("-5")
  }

  def tickQueue = {
    while (true) {
      queue.offer(s"Pushing ${TickingProducer.count}")
      Thread.sleep(2000)
    }
  }

  def tryQueueCallback(f: String => Unit) = {
    source1.to(Sink.foreach(s => f(s))).run()
  }
}

object TickingProducer {
  private var counter = 0

  def count = {
    counter = counter + 1
    counter
  }
}
