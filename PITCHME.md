## Scala Shot
## <span style="color:#e49436">Akka Aktorzy</span>

Bartosz Budnik

---
@title[Model aktorów]
#### Model aktorów

- 1973 - Carl Hewitt, Peter Bishop, Richard Steiger.
- Wprowadza abstrakcję pozwalającą skupić się na problemie podczas pisania wielowątkowych projektów. |
- Pozwala prosto pisać scalowalne, samo leczące się systemy. |

---
@title[Charakterystyka aktorów]
#### Charakterystyka aktorów
- Posiadają własny stan, zachowania, oraz skrzynkę pocztową. 
- Komunikują się pomiędzy sobą za pomoca niemutowalnych wiadomości. | 
- Aktorzy mogą zmieniać swoje zachowania oraz stan wewnętrzny. |
- Tworzą hierarchiczną strukturę. |

+++
@title[Przykład hierarchii]


![Przykładowa hierarchia](assets/arch_tree_diagram.png)
Przykładowa hierarchia aktorów

---
@title[Prosty przykład...]
#### Prosty przykład...
```scala
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

  private var requests: Int = 0

  override def receive: Receive = loud

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
```

@[4-9](Deklaracja wiadomości.)
@[12-25](Klasa aktora.)
@[17-22](Funkcja której zadaniem jest przetwarzanie przychodzących wiadomości.)
@[20](Become zmieni zachowanie aktora na to zdefiniowane w funkcji quiet)
@[25](Unbecome zdejmie ze stosu zachowanie.)

+++
@title[... oraz jego wywołanie]
#### ... oraz jego wywołanie

```scala
object Repeater extends App {
  val system = ActorSystem("RepeaterSystem")

  val actorRef: ActorRef = system.actorOf(RepeaterActor.props, "repeater")

  actorRef tell RepeaterActor.SayHi()
  actorRef tell RepeaterActor.Say("ugabuga")

  actorRef tell RepeaterActor.BeQuiet()
  actorRef tell RepeaterActor.SayHi()
  actorRef tell RepeaterActor.Say("ugabuga")

  system.terminate()
}
```

@[2](Niezbędne jest stworzenie systemu aktorów.)
@[4](Dopiero wtedy będziemy mogli zainstancjonowac nowego aktora.)
@[6-11](Komunikacja z aktorem.)

---
@title[Jak to wszystko działa?]
#### Jak to wszystko działa?

![Cykl życia wiadomości](assets/akka-message-lifecycle.png)

---
@title[Komunikacja z aktorami]
#### Jak się z nimi komunikować?

```scala
//pattern tell
actor.tell(new Say("ugabuga"))

//pattern ask
implicit val timeout: Timeout = Timeout(2.seconds)
val future: Future[String] = actor.ask(SayHi()).mapTo[String]
```

@[1-2](Wysyłanie wiadomości w stylu <i>fire and forget</i>. Zalecany sposób komunikacji z aktorami.)
@[4-6](Wysłanie wiadomość z oczekiwaniem na odpowiedź. Ma swoje wady.)

---
@title[Cameo patern]
#### Cameo pattern
```scala
class DetailsManager(actor1: ActorRef, actor2: ActorRef) extends Actor {
  override def receive: Receive = {
    case GetDetails() =>
      val handler = context.actorOf(DetailsHandler.props(sender()))
      actor1 tell(Factorial(100000000), handler)
      actor2 tell(RandomVal(2000), handler)
    case _ =>
  }
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
```

@[1-9](Zadaniem DetailsManager jest zebranie wszystkich niezbędnych danych z róznych serwisów.)
@[4](Manager tworzy dziecko, którego jedynym zadaniem będzie odebranie wszystkich niezbędnych danych.)
@[5-6](Do aktorów wysyłamy wiadomości z rządaniami obliczeń i ustawiamy DetailsHandera jako nadawcę.)
@[10](Zapisujemy orginalnego nadawcę żądania do którego przekażemy rezultaty.)
@[21-25](Przy każdej nowej wiadomości sprawdzamy czy posiadamy już wszystkie dane i odpowiednio na to reagujemy.)
@[27-30](Po odpowiedzi aktor zostanie zniszczony.)

---
@title[Podsumowanie]
## Podsumowanie
---
@title[Koniec]
## Koniec
<!-- TODO: Literówek jest pełno!!!! -->
## <span style="color:#e49436">Dziekuję za uwagę</span>
