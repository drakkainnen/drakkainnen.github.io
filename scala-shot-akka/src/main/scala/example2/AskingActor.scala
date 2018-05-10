package example2

import akka.actor.{Actor, Props}
import akka.util.Timeout
import example2.AskingActor.{QuickOperation, Sum}
import example2.SleepingActor.GetRandom

object AskingActor {
  def props: Props = Props[AskingActor]

  case class Sum()
  case class QuickOperation()

}

class AskingActor extends Actor {

  import akka.pattern.ask
  import akka.pattern.pipe
  import scala.concurrent.duration._
  import context.dispatcher

  implicit val timeout = Timeout(2 seconds)

  private val actorA = context.actorOf(SleepingActor.props, "SleepingA")
  private val actorB = context.actorOf(SleepingActor.props, "SleepingB")
  private val actorC = context.actorOf(SleepingActor.props, "SleepingC")

  var counter = 0

  override def receive: Receive = {
    case QuickOperation() => sender() ! increment()
    case Sum() =>
      val sumResult = for {
        futureA <- ask(actorA, GetRandom(2000)).mapTo[Long]
        futureB <- ask(actorB, GetRandom(2000)).mapTo[Long]
        futureC <- ask(actorC, GetRandom(2000)).mapTo[Long]
      } yield futureA + futureB + futureC
      sumResult pipeTo sender()
    case _ =>
  }

  private def increment(): Int = {
    counter += 1
    counter
  }

}
