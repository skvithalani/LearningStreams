package understandingstreams

import akka.actor.ActorSystem
import akka.stream.scaladsl.{BroadcastHub, Keep, Sink, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

object UnderstandingStreams extends App{
  implicit val actorSystem = ActorSystem("as")
  implicit val mat = ActorMaterializer()

  //Source with queue
  val sourceQ = Source.queue[Int](16, OverflowStrategy.dropNew)

  //Source with broadcast
  val (queueBd, sourceBd) = sourceQ.toMat(BroadcastHub.sink(16))(Keep.both).run()

  //Graph with Sequence Sink
  val graph = sourceQ.take(5).toMat(Sink.seq)(Keep.both)
  val (queueSeq, seqF) = graph.run()
  val (queueSeq1, seqF1) = graph.run()

  val (queueForEach, doneF) = sourceQ.toMat(Sink.foreach(x => println(s"Foreach Sink : - $x")))(Keep.both).run()

  //Source with filter
  val sourceFilter = sourceQ.filter(x => x % 2 == 0)
  val (matSourceFilter, filterQueueF) = UnderstandingStreams().splitMat(sourceFilter)

  filterQueueF.foreach(queue => {
    queue.offer(1)
    queue.offer(2)
    queue.offer(3)
    queue.offer(4)
    queue.offer(5)
  })

    queueForEach.offer(1)
    queueForEach.offer(2)
    queueForEach.offer(3)
    queueForEach.offer(4)
    queueForEach.offer(5)

  val filterSeqF = matSourceFilter.take(2).runWith(Sink.seq)
  filterSeqF.foreach(x => println(s"Filter Source Sequence Sink : - $x"))

  seqF.foreach(x => println(s"Sequence Sink : - $x"))
  seqF1.foreach(x => println(s"Sequence Sink1 : - $x"))

  queueSeq.offer(1)
  queueSeq.offer(2)
  queueSeq.offer(3)
  queueSeq.offer(4)
  queueSeq.offer(5)

  queueSeq1.offer(1)
  queueSeq1.offer(2)
  queueSeq1.offer(3)
  queueSeq1.offer(4)
  queueSeq1.offer(5)

  actorSystem.terminate()
}

case class UnderstandingStreams() {

  def splitMat(filter : Source[Int, SourceQueueWithComplete[Int]]) = {
    val promise = Promise[SourceQueueWithComplete[Int]]
    val filter1 = filter.mapMaterializedValue(mat => {
      promise.trySuccess(mat)
      mat
    })
    (filter1, promise.future)
  }
}
