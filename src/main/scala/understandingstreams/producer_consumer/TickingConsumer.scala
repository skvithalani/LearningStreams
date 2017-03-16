package understandingstreams.producer_consumer

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, KillSwitches}
import akka.stream.scaladsl.{Keep, Sink}

case class TickingConsumer() {

  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()

  val source = TickingProducer().getSource.viaMat(KillSwitches.single)(Keep.right)

  val graph1 = source.to(Sink.foreach(println))
  val graph2 = source.to(Sink.foreach(x => println(s"-- $x")))

  val switch1 = graph1.run

  Thread.sleep(4000)
  val switch2 = graph2.run
  Thread.sleep(2000)
  switch2.shutdown()
}
