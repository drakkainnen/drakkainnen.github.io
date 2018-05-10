package example2

import akka.actor.{ActorRef, ActorSelection, ActorSystem, Inbox}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import example2.AskingActor.{QuickOperation, Sum}

import scala.concurrent.{ExecutionContext, Future}

object AskingExample extends App {

  val system = ActorSystem("askingExample")
  implicit val eContext: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout.durationToTimeout(2.seconds)

  private val actor: ActorRef = system.actorOf(AskingActor.props, "actor")

  for {
    i <- 0 to 10
    result <- (actor ask Sum()).mapTo[Long]
  } {
    println(s"For $i - $result")
  }

  private val future: Future[Any] = actor.ask(QuickOperation())
  future.mapTo[Long]
    .foreach(println(_))

}
