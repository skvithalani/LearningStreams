package understandingstreams.producer_consumer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.duration.DurationDouble
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
case class Consumer() {

  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()

  private var shouldIContinue = false
  private val source = Producer().getSource

  val doneF = source.runForeach(s => s match {
    case "Unresolved" => {
      shouldIContinue = false
      println("Unresolved")
    }
    case "ResolvedAkka" => {
      shouldIContinue = true
      println("Resolved")
    }
    case "ResolvedHttp" => {
      shouldIContinue = true
      println("Resolved http")
    }
    case "ResolvedTcp" => {
      shouldIContinue = true
      println("Resolved Tcp")
    }
    case "Resolved" => {
      shouldIContinue = true
      println("Resolved")
    }
    case "Removed" => {
      shouldIContinue = false
      println("Removed")
    }
  })

  doneF.onComplete(x => {
    println(s"done")
    println(s"done $x")
    as.terminate()
  })

  val result = Await.result(doneF, 20.seconds)
  as.terminate()
}
