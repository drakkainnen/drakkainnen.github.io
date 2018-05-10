package example3

import akka.actor.{Actor, ActorRef, Props}
import example3.ComputingActor.{Factorial, RandomVal}
import example3.DetailsManager.GetDetails

object DetailsManager {

  def props(actor1: ActorRef, actor2: ActorRef) = Props(new DetailsManager(actor1, actor2))

  case class GetDetails()

}

class DetailsManager(actor1: ActorRef, actor2: ActorRef) extends Actor {
  override def receive: Receive = {
    case GetDetails() =>
      val handler = context.actorOf(DetailsHandler.props(sender()))
      actor1 tell(Factorial(100000000), handler)
      actor2 tell(RandomVal(2000), handler)
    case _ =>
  }
}

