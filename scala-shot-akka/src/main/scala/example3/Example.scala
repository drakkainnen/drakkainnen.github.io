package example3

import akka.actor.Status.Failure
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import example3.DetailsHandler.DetailsResult

import scala.concurrent.duration._
import example3.DetailsManager.GetDetails

import scala.util.Success

object Example extends App {
  val system = ActorSystem("cameoExample")
  implicit val executionContext = system.dispatcher

  private val actor1: ActorRef = system.actorOf(Props[ComputingActor], "a1")
  private val actor2: ActorRef = system.actorOf(Props[ComputingActor], "a2")
  private val detailsManager: ActorRef = system.actorOf(DetailsManager.props(actor1, actor2), "detailsManager")

  detailsManager.ask(GetDetails())(4.seconds)
    .mapTo[DetailsResult]
    .foreach(println(_))
}
