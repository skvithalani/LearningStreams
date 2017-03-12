import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source


implicit val actorSystem = ActorSystem("as")
implicit val mat = ActorMaterializer()


val source = Source(1 to 10).runForeach(println)
