package understandingstreams.ticking_producer_consumer

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink}
import akka.stream.{ActorMaterializer, KillSwitches}

case class TickingConsumer() {

  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()

  private val producer = TickingProducer()
  val source = producer.getTickingSource.viaMat(KillSwitches.single)(Keep.right)

  val broadcastSource = producer.getBroadcastSource

 /* val graph1 = source.to(Sink.foreach(println))
  val graph2 = source.to(Sink.foreach(x => {
    println("Hello")
    Thread.sleep(4000)
    println(s"-- $x")
  }))

  val switch2 = graph2.run
  val switch1 = graph1.run*/
 /* Thread.sleep(2000)
  switch2.shutdown()

  producer.tryCallback(println)
  producer.tryCallback(println)*/

 /* val graph1 = producer.getBroadcastSource.to(Sink.foreach(x => println(s"1 - $x")))
  val graph3 = producer.getBroadcastSource.to(Sink.foreach(x => println(s"3 - $x")))
  val graph2 = producer.getBroadcastSource.to(Sink.foreach(x => {
    println("Hello")
    Thread.sleep(6000)
    println(s"2 - $x")
  }))

  graph2.run
  graph3.run
  graph1.run*/

  producer.tryQueueCallback(s => {
    Thread.sleep(4000)
    println(s"1 -$s")
  })
  producer.tryQueueCallback(s => println(s"2 - $s"))

  producer.tryQueueCallback(s => println(s"3 - $s"))

  producer.tickQueue

}
