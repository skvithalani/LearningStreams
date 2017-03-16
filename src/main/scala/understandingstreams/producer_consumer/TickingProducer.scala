package understandingstreams.producer_consumer

import akka.stream.scaladsl.Source

import scala.concurrent.duration.DurationDouble

case class TickingProducer() {

  private var c = 0
  private val tickingSource = Source.tick(1.seconds, 2.seconds, {
    c = counter(c)
    s"A ticking message every 2 seconds $c"
  })

  private val normalSource = Source(1 to 10)

  def getSource = tickingSource

  def counter (initVal : Int) = {
    initVal + 1
  }
}
