package understandingstreams.producer_consumer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
case class Consumer1() {

  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()

  private val demo = "hello"
  private var shouldIContinue = false

  private def meth(s: String) = {
    s match {
      case "Unresolved" => {
        shouldIContinue = false
        println("--Unresolved")
        println(s"ShouldIContinue : - $shouldIContinue -- $demo")
      }
      case "ResolvedAkka" => {
        shouldIContinue = true
        println("--Resolved akka")
        println(s"ShouldIContinue : - $shouldIContinue -- $demo")
      }
      case "ResolvedHttp" => {
        shouldIContinue = true
        println("--Resolved http")
        println(s"ShouldIContinue : - $shouldIContinue -- $demo")
      }
      case "ResolvedTcp" => {
        shouldIContinue = true
        println("--Resolved Tcp")
        println(s"ShouldIContinue : - $shouldIContinue -- $demo")
      }
      case "Resolved" => {
        shouldIContinue = true
        println("--Resolved")
        println(s"ShouldIContinue : - $shouldIContinue -- $demo")
      }
      case "Removed" => {
        shouldIContinue = false
        println("--Removed")
        println(s"ShouldIContinue : - $shouldIContinue -- $demo")
      }
    }
  }

  Producer().tryCallback(meth)


}
