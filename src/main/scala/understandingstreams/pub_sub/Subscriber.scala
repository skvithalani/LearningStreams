package understandingstreams.pub_sub

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink}

case class Subscriber() {

  private var shouldIContinue = false
  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()

  private def meth(s: String) = {
    s match {
      case "Unresolved" => {
        shouldIContinue = false
        println("--Unresolved")
        println(s"ShouldIContinue : - $shouldIContinue")
      }
      case "ResolvedAkka" => {
        shouldIContinue = true
        println("--Resolved akka")
        println(s"ShouldIContinue : - $shouldIContinue")
      }
      case "ResolvedHttp" => {
        shouldIContinue = true
        println("--Resolved http")
        println(s"ShouldIContinue : - $shouldIContinue")
      }
      case "ResolvedTcp" => {
        shouldIContinue = true
        println("--Resolved Tcp")
        println(s"ShouldIContinue : - $shouldIContinue")
      }
      case "Resolved" => {
        shouldIContinue = true
        println("--Resolved")
        println(s"ShouldIContinue : - $shouldIContinue")
      }
      case "Removed" => {
        shouldIContinue = false
        println("--Removed")
        println(s"ShouldIContinue : - $shouldIContinue")
      }
    }
  }

  val publisher = Publisher()
  val unresolved = publisher.consumeUnResolved()

  publisher.broadcastSource.runWith(Sink.ignore)

  unresolved.runForeach(println)

  Thread.sleep(5000)

  private val resolvedTcp = publisher.consumeResolvedTcp()
  val switch = resolvedTcp.toMat(Sink.foreach(println))(Keep.left).run()
  switch.shutdown()

  val switch1 = resolvedTcp.toMat(Sink.foreach(println))(Keep.left).run()

  println("Going to akka")
  publisher.consumeResolvedAkka().runForeach(println)

  println("Going to removed")
  publisher.consumeRemoved(meth)

}
