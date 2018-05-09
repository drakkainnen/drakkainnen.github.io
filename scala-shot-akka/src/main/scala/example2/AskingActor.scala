package example2

import akka.actor.{Actor, Props}
import akka.util.Timeout
import example2.AskingActor.{QuickOperation, Sum}
import example2.SleepingActor.GetRandom

object AskingActor {
  def props(number: Int) = Props(new AskingActor(number))
  case class Sum()
  case class QuickOperation()
}

class AskingActor(numberOfActors: Int) extends Actor {
  import akka.pattern.ask
  import akka.pattern.pipe
  import scala.concurrent.duration._
  import context.dispatcher

  implicit val timeout = Timeout.durationToTimeout(2 seconds)

  val actorA = context.actorOf(SleepingActor.props, "SleepingA")
  val actorB = context.actorOf(SleepingActor.props, "SleepingB")
  val actorC = context.actorOf(SleepingActor.props, "SleepingC")

  var counter = 0

  override def receive: Receive = {
    case QuickOperation() => sender() ! increment()
    case Sum() =>
      val sumResult = for {
        futureA <- (actorA ask GetRandom(2000)).mapTo[Long]
        futureB <- (actorA ? GetRandom(2000)).mapTo[Long]
        futureC <- (actorA ? GetRandom(2000)).mapTo[Long]
      } yield (futureA + futureB + futureC)
      sumResult pipeTo sender()
    case _ =>
  }

  private def increment(): Int = {
    counter += 1
    counter
  }

}
