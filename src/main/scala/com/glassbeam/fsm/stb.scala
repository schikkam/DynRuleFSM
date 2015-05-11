package com.glassbeam.fsm

import scala.tools.reflect.ToolBox
import scala.collection.mutable.ListBuffer
import akka.actor.ActorRef
import akka.actor.{ Actor, FSM }
import scala.collection.immutable.HashMap
/*
 * this code creates an actor from the rules definition 
 * we invoke the actor by sending a msg 
 */


sealed trait State
case object Idle extends State
case object InitRule extends State
sealed trait Data
case object Uninitialized extends Data
case object Initialize extends Data
case object Start extends Data
case object EvaluateRule extends State
case class RowMap(m: HashMap[String, String]) extends Data

object RuleTemplate{

    val actorName = "A"

  val ruleTemplate = s"""
    import scala.collection.mutable.ListBuffer
    import akka.actor.Actor
    import akka.actor.Props
    import akka.actor.FSM
    import scala.collection.immutable.HashMap
    import com.glassbeam.fsm._
   
    class ${actorName} extends Actor with FSM[State, Data] {

    import context._

    startWith(Idle, Uninitialized)

    when(Idle){
      case Event(Start, Uninitialized) =>
        println("-> Idle ")
        goto(InitRule)
    }
    when(InitRule) {
      case Event(Initialize, _) => {
        println("-> InitRule")
        goto(EvaluateRule)
      }
      case Event(_, _) =>
        println("InitRule, Not Handled")
        stay
    }
    when(EvaluateRule) {
      case Event(RowMap(m), _) =>
        println("-> EvaluateRule")
        stay
    }
   
    whenUnhandled {
      case Event(e, s) =>
        println("State = Unhandled, Received unhandled request {} in state {}/{}" + e + stateName + s)
        stay
    }

    onTransition {
      case Idle -> InitRule => println(s"Moving from Idle to InitRule for rule ")
      case InitRule -> EvaluateRule => println(s"Moving from InitRule to EvaluateRule rule ")
    }
    initialize()
    }
    import akka.actor.ActorSystem
    val system = ActorSystem("mySystem")
    val myActor = system.actorOf(Props(new ${actorName}()), name = "${actorName}")
    myActor
    """

}

object Main extends App {

  val tb = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader).mkToolBox()

  val actorName = "A"

  import RuleTemplate._
  // printing the generated code for the RuleActor 
  println(ruleTemplate)

  // Iam using scala toolbox to parse the ruleTemplate
  val tree = tb.parse(ruleTemplate)
  //val actor :ActorRef = tb.compile(tree)().asInstanceOf[ActorRef]
  // Iam casting the dynmically created actor as ActorRef  
  val actor: ActorRef = tb.eval(tree).asInstanceOf[ActorRef]
  
  // for demo purpose Iam just calling the actor 
  println("Started...")
  actor ! Start
  actor ! Start
  actor ! Initialize
  actor ! RowMap(HashMap[String, String]("id1" -> "abc", "id2" -> "xyz", "id3" -> "pqr")) 
}
