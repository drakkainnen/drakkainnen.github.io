package example3

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorRef, Props}
import example3.ComputingActor.{FactorialResult, RandomResult}
import example3.DetailsHandler.DetailsResult

import scala.concurrent.duration._

object DetailsHandler {
  def props(originalSender: ActorRef) = Props(new DetailsHandler(originalSender))

  case class DetailsResult(factorial: BigDecimal, random: Long)

}

class DetailsHandler(val originalSender: ActorRef) extends Actor {
  import context.dispatcher

  val cancellable = context.system.scheduler.scheduleOnce(10.seconds, self, "timeout")

  var factorial: Option[BigDecimal] = None
  var randomNumber: Option[Long] = None

  override def receive: Receive = {
    case FactorialResult(res) =>
      factorial = Some(res)
      check()
    case RandomResult(res) =>
      randomNumber = Some(res)
      check()
    case "timeout" =>
      sendResponseAndShutdown(Failure(new Exception("timeout occured")))
  }

  def check() = (factorial, randomNumber) match {
    case (Some(fac), Some(rand)) =>
      sendResponseAndShutdown(DetailsResult(fac, rand))
    case _ =>
  }

  def sendResponseAndShutdown(resp: Any) = {
    cancellable.cancel()
    originalSender ! resp
    context.stop(self)
  }

}

