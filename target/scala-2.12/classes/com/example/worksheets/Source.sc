import java.util.concurrent.{Executors, ScheduledExecutorService}

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.{Done, NotUsed}
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
//import system.dispatcher

val s1 = Source.empty

val s2 = Source.single("1 2 3")

val s3 = Source(1 to 10)

val s4 = Source.fromFuture(Future("This is from future"))


val threadpool: ScheduledExecutorService = Executors.newScheduledThreadPool(8)

implicit val ec = ExecutionContext.fromExecutorService(threadpool)
implicit val system: ActorSystem = ActorSystem("TestSystem", ConfigFactory.load())
implicit val mat : Materializer = ActorMaterializer()
val f: Future[Done] = s2.runForeach(println)