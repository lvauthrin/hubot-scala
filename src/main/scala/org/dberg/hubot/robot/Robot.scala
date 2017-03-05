package org.dberg.hubot.robot

import org.dberg.hubot.utils.Logger
import org.dberg.hubot.adapter.BaseAdapter
import org.dberg.hubot.middleware.{Middleware, MiddlewareError, MiddlewareSuccess}
import org.dberg.hubot.models.{Listener, Message}
import org.dberg.hubot.utils.Helpers._

trait RobotComponent {

  val robotService: RobotService
  val adapter: BaseAdapter
  val listeners: Seq[Listener]
  val middleware: Seq[Middleware]

  class RobotService {


    private def processMiddlewareRec(message: Message,
                                     m: Seq[Middleware],
                                     prevResult: Either[MiddlewareError,MiddlewareSuccess] = Right(MiddlewareSuccess())): Either[MiddlewareError,MiddlewareSuccess] = m match {
      case Nil => prevResult
      case h :: t if prevResult.isRight => processMiddlewareRec(message,t,h.execute(message))
      case _ =>  prevResult
    }

    def processMiddleware(message: Message) = processMiddlewareRec(message: Message, middleware)

    
    def processListeners( message: Message) = {
      listeners.foreach { l => Logger.log("Processing message through listener " + l.toString);   l.call(message) }
    }

    val hubotName = getConfString("hubot.name","hubot")


    def receive(message: Message) = {
      Logger.log("Received message " + message)
      //Loop through middleware, halting if need be
      //then send to each listener
      processMiddleware(message) match {
        case Left(x) => Logger.log("Sorry, middleware error " + x.error)
        case Right(x) =>
          Logger.log("Middleware passed")
          processListeners(message)
      }
    }

    def send(message: Message) =
      adapter.send(message)

    def run() = {
      adapter.run()
    }
  }
}