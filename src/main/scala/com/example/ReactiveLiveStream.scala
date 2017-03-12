package com.example

import java.util.Calendar

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future

object ReactiveLiveStream extends App{


  final case class Author(handle : String)

  final case class Hashtag(name : String)

  final case class Tweet(author : Author, timestamp : Long, body : String) {



    val comment: Array[String] = body.split(" ")

    val hashtagArray: Array[Hashtag] = comment.collect({
      case tag if(tag startsWith("#")) => Hashtag(tag)
    })

    val hashtags : Set[Hashtag] = hashtagArray.toSet
  }

  val akkaTag = Hashtag("#akka")

  implicit val system = ActorSystem("twitter_setup")
  implicit val actorMaterializer = ActorMaterializer()

  val saloni = Author("Saloni")
  val monali = Author("Monali")
  val bhupendra = Author("Bhupendra")
  val tweet3 = Tweet(monali, Calendar.getInstance().getTimeInMillis, "hello #akka")
  val tweet1 = Tweet(saloni, Calendar.getInstance().getTimeInMillis, "hello #scala")
  val tweet2 = Tweet(bhupendra, Calendar.getInstance().getTimeInMillis, "hello #akka")

  val tweets : Source[Tweet, NotUsed] = Source.fromIterator[Tweet](() => List(tweet1, tweet2, tweet3).iterator)

  val authors: Source[Author, NotUsed] = tweets
                                              .filter(_.hashtags.contains(akkaTag))
                                              .map(_.author)

    authors.runWith(Sink.foreach(println))


  val hashTags : Source[Hashtag, NotUsed] = tweets.filter(_.hashtags.contains(akkaTag)).mapConcat(_.hashtags.toList)

  var authorsSink: Sink[Any, Future[Done]] = Sink.foreach(println)
  val hashTagSink : Sink[Any, Future[Done]] = Sink.foreach(println)

  val graph = GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val bcast: UniformFanOutShape[Tweet, Tweet] = b.add(Broadcast[Tweet](2))
    tweets ~> bcast.in
    bcast.out(0) ~> Flow[Tweet].map(_.author).toMat(authorsSink)(Keep.right)
    bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList).toMat(hashTagSink)(Keep.right)

    ClosedShape
  }
  val runnableGraph: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(graph)
  runnableGraph.run()
  system.terminate()
}
