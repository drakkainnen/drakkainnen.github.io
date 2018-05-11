package example3

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
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

//import akka.pattern.ask
//import akka.pattern.pipe
//import scala.concurrent.duration._
//
//class DetailsManager(actor1: ActorRef) extends Actor {
//  import context.dispatcher
//
//  implicit val timeout = Timeout(4.seconds)
//
//  override def receive: Receive = {
//    case GetDetails() =>
//      val future1 = ask(actor1, Factorial(1)).mapTo[BigDecimal]
//      val future2 = ask(actor1, Factorial(5)).mapTo[BigDecimal]
//      val future3 = ask(actor1, Factorial(10)).mapTo[BigDecimal]
//      val future4 = ask(actor1, Factorial(1000)).mapTo[BigDecimal]
//
//      val result = future1.flatMap(v1 => {
//        future2 flatMap { v2 =>
//          future3 flatMap { v3 =>
//            future4 map { v4 =>
//              (v1, v2, v3, v4)
//            }
//          }
//        }
//      })
//      result pipeTo sender()
//
//    case _ =>
//  }
//}
