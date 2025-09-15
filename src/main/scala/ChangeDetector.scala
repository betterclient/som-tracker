package io.github.betterclient.tracker

import changes.detectors
import html.{SomItem, SomItemParser, itemRW}

import com.slack.api.methods.MethodsClient
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.{HeaderBlock, LayoutBlock, SectionBlock}
import upickle.default.{read, write}

import java.io.{File, FileInputStream, FileOutputStream}
import scala.collection.mutable.ListBuffer
import scala.util.Using

object ChangeDetector {
    def detectChanges(client: MethodsClient): Unit = {
        println("Stage 0")
        val old = Using(
            FileInputStream("items.json")
        ) {
            _.readAllBytes()
        }
            .map(String(_)) //byte[] -> string
            .map(read[List[SomItem]](_)) //string -> List
            .getOrElse(List()) //you suck
        
        if(old.isEmpty) {
            val current = SomItemParser.parseAll().toList
            File("items.json").delete()
            Using(FileOutputStream("items.json")) {
                _.write(write(current, indent = 4).getBytes)
            }
            return //don't spam if something went wrong, just write correct and wait for next
        }

        println("Stage 1")
        val current = SomItemParser.parseAll().toList

        var ping = false
        println("Stage 2")
        for ((oldItem, newItem) <- old.map(i => (i, current.find(_.id == i.id).orNull))) {
            var changes = false
            val finalMessage = ListBuffer[LayoutBlock]()
            if(newItem == null) {
                //deleted!
                finalMessage.addOne(
                    HeaderBlock.builder().text(PlainTextObject(s":win10-trash: ${oldItem.name}\n", true)).build()
                )
                changes = true; ping = true
            } else {
                for (dc <- detectors) {
                    val out = dc.detect(oldItem, newItem)
                    if(out.changed) changes = true
                    if(out.ping) ping = true
                    out.block.map(finalMessage.addOne)
                }
            }

            if(changes) {
                //send message
                ChangeAnnouncer.announce(client, finalMessage.toList, oldItem)
            }
        }

        println("Stage 3")
        for(newItem <- current.filter(i => !old.exists(i.id == _.id))) {
            ping = true
            val finalMessage = ListBuffer[LayoutBlock]()
            buildNewItemMessage(finalMessage, newItem)
            ChangeAnnouncer.announce(client, finalMessage.toList, newItem)
        }

        if(ping) ChangeAnnouncer.ping(client)
        
        println("Stage 4")
        File("items.json").delete()
        Using(FileOutputStream("items.json")) {
            _.write(write(current, indent = 4).getBytes)
        }
    }

    private def buildNewItemMessage(blocks: ListBuffer[LayoutBlock], item: SomItem): Unit = {
        blocks += HeaderBlock.builder()
            .text(PlainTextObject.builder().text(s":new: ${item.name}").emoji(true).build())
            .build()

        blocks += SectionBlock.builder()
            .text(PlainTextObject.builder().text(item.description).build())
            .build()

        val pricesText =
            s"""|:flag-us: United States: ${item.priceUS}
                |:flag-eu: :gb: Europe + United Kingdom: ${item.priceEU}
                |:flag-in: India: ${item.priceIN}
                |:flag-ca: Canada: ${item.priceCA}
                |:flag-au: ∀nsʇɹɐlᴉɐ: ${item.priceAU}
                |:earth: Rest of the world: ${item.priceXX}""".stripMargin

        blocks += SectionBlock.builder()
            .text(PlainTextObject.builder().text(pricesText).build())
            .build()

        val stockText = item.stock match {
            case -1 => "Infinite"
            case 0 => "Out of stock"
            case n => n.toString
        }

        blocks += SectionBlock.builder()
            .text(PlainTextObject.builder().text(s"Stock: $stockText").build())
            .build()
    }
}