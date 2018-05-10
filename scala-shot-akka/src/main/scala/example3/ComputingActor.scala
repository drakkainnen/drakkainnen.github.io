package example3

import akka.actor.Actor
import example3.ComputingActor._

import scala.annotation.tailrec
import scala.util.Random

object ComputingActor {

  case class Factorial(n: Int)
  case class RandomVal(max: Int)

  case class FactorialResult(res: BigDecimal)
  case class RandomResult(res: Long)

}
class ComputingActor extends Actor {

  val random = new Random()

  override def receive: Receive = {
    case Factorial(n) =>
      println("Factorial")
      sender() ! FactorialResult(factorial(n))
    case RandomVal(max) =>
      println("Random")
      sender() ! RandomResult(random.nextLong() % max)
  }

  def factorial(n: Int) = {
    @tailrec
    def loop(n: Int, r: BigDecimal): BigDecimal = {
      if (n == 1) r else loop(n - 1, n * r)
    }
    loop(n, 1)
  }
}
