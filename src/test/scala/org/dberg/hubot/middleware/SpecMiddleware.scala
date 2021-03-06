package org.dberg.hubot.middleware

import com.typesafe.scalalogging.StrictLogging
import org.dberg.hubot.{ HubotBase }
import org.dberg.hubot.models.Message

class SpecMiddleware(hubot: HubotBase) extends Middleware(hubot) with StrictLogging {

  def execute(message: Message) = {
    if (message.body.contains("blacklist")) {
      Left(MiddlewareError("Sorry this is blocked"))
    } else {
      Right(MiddlewareSuccess())
    }
  }
}