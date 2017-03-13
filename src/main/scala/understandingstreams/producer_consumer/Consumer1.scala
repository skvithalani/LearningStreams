package understandingstreams.producer_consumer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

case class Consumer1() {

  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()

  private var shouldIContinue = false

  Producer().getSource.runForeach(s => s match {
    case "Unresolved" => {
      shouldIContinue = false
      println("--Unresolved")
    }
    case "ResolvedAkka" => {
      shouldIContinue = true
      println("--Resolved akka")
    }
    case "ResolvedHttp" => {
      shouldIContinue = true
      println("--Resolved http")
    }
    case "ResolvedTcp" => {
      shouldIContinue = true
      println("--Resolved Tcp")
    }
    case "Resolved" => {
      shouldIContinue = true
      println("--Resolved")
    }
    case "Removed" => {
      shouldIContinue = false
      println("--Removed")
    }
  })

}
