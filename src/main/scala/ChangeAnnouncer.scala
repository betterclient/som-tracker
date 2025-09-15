package io.github.betterclient.tracker

import html.SomItem

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.{ContextBlock, DividerBlock, ImageBlock, LayoutBlock}

import scala.jdk.CollectionConverters.*

object ChangeAnnouncer {
    def announce(methods: MethodsClient, finalMessage: List[LayoutBlock], item: SomItem): Unit = {
        val finalBlocks = finalMessage ++ Seq(
            ImageBlock.builder().imageUrl(item.image).altText("Som tracker").build(),
            DividerBlock.builder().build(),
            ContextBlock.builder().elements(
                List(
                    MarkdownTextObject(s"<https://summer.hackclub.com/shop/items/${item.id}/buy|Buy>", false)
                ).asJava
            ).build()
        )
        
        println("Stage 5")
        methods.chatPostMessage(ChatPostMessageRequest
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
}
