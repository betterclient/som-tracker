package io.github.betterclient.tracker

import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.bolt.{App, AppConfig}
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import upickle.{ReadWriter, macroRW, read}

import java.io.FileInputStream
import scala.io.Source
import scala.util.Try

var config: Config = null

@main
def main(): Unit = {
    config = Try {
        val stream = new FileInputStream("config.json")
        try read[Config](Source.fromInputStream(stream).mkString)
        finally stream.close()
    }.getOrElse(throw new RuntimeException("No config."))

    println("Starting..")
    val config1 = AppConfig()
    config1.setSigningSecret(config.signingSecret)
    config1.setSingleTeamBotToken(config.botToken)
    val app = App(config1)
    val socket = SocketModeApp(config.appToken, app)
    socket.startAsync()

    while(true) {
        try {
            ChangeDetector.detectChanges(app.getClient)
        } catch {
            case e: Exception =>
                println("Error in parsing")
                app.getClient.chatPostMessage(ChatPostMessageRequest.builder().text("fix me betterclient").channel(config.debugChannel).build())
                e.printStackTrace()
        }
        Thread.sleep(3 * 60 * 1000) //3min
    }
}

implicit val configRW: ReadWriter[Config] = macroRW
case class Config
(
    signingSecret: String,
    botToken: String,
    appToken: String,
    cookie: String,
    botChannel: String,
    debugChannel: String
)