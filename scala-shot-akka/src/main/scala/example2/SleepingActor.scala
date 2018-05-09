package example2

import java.util.Calendar

import akka.actor.{Actor, Props}
import example2.SleepingActor.GetRandom

import scala.concurrent.Future
import scala.util.Random

object SleepingActor {
  def props = Props[SleepingActor]
  case class GetRandom(waitMax: Int)
}

class SleepingActor extends Actor {
  private val randomGenerator = new Random(Calendar.getInstance().getTimeInMillis())

  override def receive: Receive = {
    case GetRandom(waitMax) =>
      val orgSender = sender()
      Future(Thread.sleep(randomGenerator.nextLong() % waitMax))
        .onComplete(_ => orgSender ! randomGenerator.nextLong())
    case _ =>
  }
}
