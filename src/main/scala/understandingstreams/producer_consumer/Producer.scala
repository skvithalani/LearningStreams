package understandingstreams.producer_consumer

import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.Source
import understandingstreams.common.SourceExtensions.RichSource
import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext.Implicits.global
case class Producer() {

  private val producedValues = List("Resolved", "ResolvedAkka", "ResolvedTcp", "ResolvedHttp", "Removed", "Unresolved")
  private val (source, queueF) = Source.queue[String](256, OverflowStrategy.dropNew).splitMat()
  queueF.foreach(queue => producedValues.foreach(value => queue.offer(value)))

  def getSource = {
    source
  }

  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()
  def tryCallback(f : String => Unit) {
    println(f.getClass)
    source.runForeach(s => f(s))
  }

}
//Gives a static same source to all the consumers. This way only one consumer can consume a single source
object Producer {
  private val producedValues = List("Resolved", "ResolvedAkka", "ResolvedTcp", "ResolvedHttp", "Unresolved", "Removed")
  private val (source, queueF) = Source.queue[String](256, OverflowStrategy.dropNew).splitMat()
  queueF.foreach(queue => producedValues.foreach(value => queue.offer(value)))
}
