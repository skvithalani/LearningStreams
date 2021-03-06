package understandingstreams.producer_consumer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink}

import scala.concurrent.ExecutionContext.Implicits.global
case class Consumer() {

  implicit val as = ActorSystem("system")
  implicit val mat = ActorMaterializer()

  private var shouldIContinue = false
  private val source = Producer().getSource

  //Future of splitMat gets completed on first materialization of queue
  // and not on subsequent materialization of queue.
  // So first queue only gets element in it
  val doneF = source.runForeach(s => meth(s))
  val doneF1 = source.runForeach(s => meth(s))

  val seqF = source.take(6).toMat(Sink.seq)(Keep.right).run()

  seqF.foreach(x => x.foreach(meth))

  private def meth(s: String) = {
    s match {
      case "Unresolved" => {
        shouldIContinue = false
        println("Unresolved")
      }
      case "ResolvedAkka" => {
        shouldIContinue = true
        println("Resolved akka")
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
    }
  }

  seqF.onComplete(s => as.terminate())
}
