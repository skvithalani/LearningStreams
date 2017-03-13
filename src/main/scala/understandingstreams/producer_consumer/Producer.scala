package understandingstreams.producer_consumer

import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import SourceExtensions.RichSource
import scala.concurrent.ExecutionContext.Implicits.global
case class Producer() {

  private val producedValues = List("Resolved", "ResolvedAkka", "ResolvedTcp", "ResolvedHttp", "Unresolved", "Removed")
  private val (source, queueF) = Source.queue[String](256, OverflowStrategy.dropNew).splitMat()

  def getSource = {
    queueF.foreach(queue => producedValues.foreach(value => queue.offer(value)))
    source
  }

}
