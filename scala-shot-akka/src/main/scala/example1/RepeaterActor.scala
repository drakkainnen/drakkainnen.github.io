package example1

import akka.actor.{Actor, Props}
import example1.RepeaterActor._

object RepeaterActor {

  def props = Props[RepeaterActor]

  case class Say(message: String)
  case class SayHi()
  case class BeQuiet()
  case class SpeakAgain()
  case class NumberOfAnswers()
}

class RepeaterActor extends Actor {
  import context.become
  import context.unbecome

  override def receive: Receive = loud

  private var requests: Int = 0

  def loud: Receive = {
    case SayHi() => println("Hi!"); increment()
    case Say(message) => println(message); increment()
    case BeQuiet() => println("I will not speak again"); become(quiet);
    case NumberOfAnswers() => println(s"Nr of answers ${requests}")
    case _ =>
  }

  def quiet: Receive = {
    case SpeakAgain() => println("Lets talk again"); unbecome();
    case _ =>
  }

  def increment() = requests += 1

}
