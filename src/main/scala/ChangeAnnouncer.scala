package io.github.betterclient.tracker

import html.SomItem

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.files.FilesUploadV2Request
import com.slack.api.model.block.composition.{MarkdownTextObject, SlackFileObject}
import com.slack.api.model.block.{ContextBlock, DividerBlock, ImageBlock, LayoutBlock}

import java.net.URI
import scala.jdk.CollectionConverters.*
import scala.util.{Try, Using}

object ChangeAnnouncer {
    def announce(methods: MethodsClient, finalMessage: List[LayoutBlock], item: SomItem, buyButton: Boolean = true): Unit = {
        val image = generateImage(methods, config.botChannel, item.image)
        val finalBlocks = finalMessage ++ {
            val blocks = scala.collection.mutable.ListBuffer[LayoutBlock]()
            image.foreach { id =>
                blocks += ImageBlock.builder().slackFile(SlackFileObject.builder().id(id).build()).altText("Som tracker").build()
                blocks += DividerBlock.builder().build()
            }

            blocks += ContextBlock.builder().elements(
                List(
                    if (buyButton) {
                        MarkdownTextObject(
                            s"<https://summer.hackclub.com/shop/items/${item.id}/buy|Buy> <https://github.com/betterclient/som-tracker|Star the repository>",
                            false
                        )
                    } else {
                        MarkdownTextObject(
                            "<https://github.com/betterclient/som-tracker|Star the repository>",
                            false
                        )
                    }
                ).asJava
            ).build()
            blocks.toSeq
        }
        
        println("Stage 5")
        Thread.sleep(1000) //file upload is async for some reason so we have to wait a bit
        val response = methods.chatPostMessage(ChatPostMessageRequest
            .builder()
            .blocks(finalBlocks.asJava)
            .channel(config.botChannel)
            .build()
        )
    }

    def ping(client: MethodsClient): Unit = {
        client.chatPostMessage(ChatPostMessageRequest
            .builder()
            .text(s"<!subteam^${config.ping}>")
            .mrkdwn(true)
            .linkNames(true)
            .channel(config.botChannel)
            .build()
        )
    }

    def generateImage(client: MethodsClient, channelID: String, url: String): Option[String] = {
        val fileDataResult = Try {
            Using(new URI(url).toURL.openStream()) { _.readAllBytes() }.getOrElse(Array[Byte](0)) //sorry not sorry
        }.recover {
            case e: Exception => Array[Byte](0)
        }

        if(fileDataResult.isFailure || fileDataResult.get.isEmpty) return Option.empty

        val uploadResult = client.filesUploadV2(
            FilesUploadV2Request.builder()
                .fileData(fileDataResult.get)
                .filename(s"hi${System.nanoTime()}.png")
                .build()
        )

        if(!uploadResult.isOk) return Option.empty

        Try {
            Option(uploadResult.getFiles.get(0).getId)
        }.recover {
            case _: Exception => Option.empty
        }.getOrElse(Option.empty)
    }
}
