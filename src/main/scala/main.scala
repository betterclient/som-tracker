package io.github.betterclient.tracker

import com.slack.api.RequestConfigurator
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.bolt.{App, AppConfig}
import com.slack.api.methods.request.chat.ChatPostMessageRequest.ChatPostMessageRequestBuilder
import org.jsoup.nodes.Node
import upickle.{ReadWriter, macroRW, read, write}

import java.io.{File, FileInputStream, FileOutputStream}
import java.util
import scala.util.Using

@main
def main(): Unit = {
    println("Starting..")
    /*val config = readConfig()
    val config1 = AppConfig()
    config1.setSigningSecret(config.signingSecret)
    config1.setSingleTeamBotToken(config.botToken)
    val app = App(config1)
    val socket = SocketModeApp(config.appToken, app)
    socket.startAsync()

    while(true) {
        Thread.sleep(60 * 1000) //1min
    }*/
    ChangeDetector.detectChanges(null)
}

def readConfig() =
    val f = File("config.json")
    if (f.exists())
        read[Config](new String(Using(new FileInputStream(f)) {
            _.readAllBytes()
        }.get))
    else
        throw RuntimeException("NO Config.")

implicit val configRW: ReadWriter[Config] = macroRW
case class Config
(
    signingSecret: String,
    botToken: String,
    appToken: String,
    cookie: String
)