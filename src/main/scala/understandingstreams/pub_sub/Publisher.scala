package understandingstreams.pub_sub

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{BroadcastHub, Keep, Sink, Source}
import scala.concurrent.ExecutionContext.Implicits.global
case class Publisher() {
  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()

  private val producedValues = List("ResolvedAkka","Resolved", "ResolvedTcp", "ResolvedHttp", "Removed", "Unresolved")
  private val source = Source.queue[String](256, OverflowStrategy.dropNew)

  val (queue, broadcastSource) = source.toMat(BroadcastHub.sink)(Keep.both).run()

  producedValues.foreach(x => queue.offer(x))


  def consume(f : String => Unit) = {
      broadcastSource.runForeach(s => f(s))
  }

  def consumeResolvedHttp(f : String => Unit) = {
    val filter = broadcastSource.filter(s => s.startsWith("ResolvedHttp"))
    val stringF = filter.runWith(Sink.head)
    stringF.onComplete(s => f(s.get))
  }

  def consumeResolvedTcp() = {
    val filter = broadcastSource.filter(s => s.startsWith("ResolvedTcp"))
    filter
  }

  def consumeResolvedAkka() = {
    val filter = broadcastSource.filter(s => s.startsWith("ResolvedAkka"))
    producedValues.foreach(x => queue.offer(x))
    filter
  }

  def consumeRemoved(f : String => Unit) = {
    val filter = broadcastSource.filter(s => s.startsWith("Removed"))
    filter.runForeach(s => f(s))
    producedValues.foreach(x => queue.offer(x))
  }

  def consumeUnResolved() = {
    val filter = broadcastSource.filter(s => s.startsWith("Unresolved"))
    filter
  }
}
