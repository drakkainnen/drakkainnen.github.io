package example2

import akka.actor.{ActorRef, ActorSelection, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import example2.AskingActor.Sum

object AskingExample extends App {

  val system = ActorSystem("askingExample")
  implicit val timeout: Timeout = Timeout.durationToTimeout(2.seconds)
  private val actor: ActorRef = system.actorOf(AskingActor.props(10), "actor")

  private val selection: ActorSelection = system.actorSelection("actor")

  for {
    i <- 0 to 10
    result <- (selection ask Sum()).mapTo[Long]
  } {
  }
}
