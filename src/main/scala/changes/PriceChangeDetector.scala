package io.github.betterclient.tracker
package changes

import html.SomItem

import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.PlainTextObject

object PriceChangeDetector extends BaseDetector {
    override def detect(oldItem: SomItem, newItem: SomItem): DetectionResult = {
        var ping = false
        var outMessage = ""
        if (newItem.priceUS != oldItem.priceUS) {
            ping = true
            outMessage += s":us: United states: ${oldItem.priceUS} -> ${newItem.priceUS}\n"
        }

        if (newItem.priceEU != oldItem.priceEU) {
            ping = true
            outMessage += s":flag-eu: :gb: Europe + United Kingdom: ${oldItem.priceEU} -> ${newItem.priceEU}\n"
        }

        if (newItem.priceIN != oldItem.priceIN) {
            ping = true
            outMessage += s":flag-in: India: ${oldItem.priceIN} -> ${newItem.priceIN}\n"
        }

        if (newItem.priceCA != oldItem.priceCA) {
            ping = true
            outMessage += s":flag-ca: Canada: ${oldItem.priceCA} -> ${newItem.priceCA}\n"
        }

        if (newItem.priceAU != oldItem.priceAU) {
            ping = true
            outMessage += s":flag-au: ∀nsʇɹɐlᴉɐ: ${oldItem.priceAU} -> ${newItem.priceAU}\n"
        }

        if (newItem.priceXX != oldItem.priceXX) {
            ping = true
            outMessage += s":earth: Rest of the world: ${oldItem.priceXX} -> ${newItem.priceXX}\n"
        }
        
        DetectionResult(
            ping, ping, Option(
                SectionBlock.builder().text(PlainTextObject(outMessage, true)).build()
            )
        )
    }
}
