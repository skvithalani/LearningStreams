package understandingstreams.common

import akka.NotUsed
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}

import scala.concurrent.{Future, Promise}

object SourceExtensions {

  implicit class RichSource(val source : Source[String, SourceQueueWithComplete[String]]) extends AnyVal {

    def splitMat() : (Source[String, NotUsed], Future[SourceQueueWithComplete[String]]) = {
      val promise = Promise[SourceQueueWithComplete[String]]
      val resultSource = source.mapMaterializedValue(mat => {
        promise.trySuccess(mat)
        NotUsed
      })

      (resultSource, promise.future)
    }

  }
}
