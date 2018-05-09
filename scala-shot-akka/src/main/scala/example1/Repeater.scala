package example1

import akka.actor.{ActorRef, ActorSystem}

object Repeater extends App {
  val system = ActorSystem("RepeaterSystem")

  val actorRef: ActorRef = system.actorOf(RepeaterActor.props, "repeater")

  actorRef ! RepeaterActor.SayHi()
  actorRef ! RepeaterActor.Say("ugabuga")

  actorRef ! RepeaterActor.BeQuiet()
  actorRef ! RepeaterActor.SayHi()
  actorRef ! RepeaterActor.Say("ugabuga")
  actorRef ! RepeaterActor.SpeakAgain()

  actorRef ! RepeaterActor.NumberOfAnswers()
  actorRef ! RepeaterActor.Say("It is time to take a pill")

  system.terminate()
}

