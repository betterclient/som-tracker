package io.github.betterclient.tracker
package changes

import html.SomItem

import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.PlainTextObject

object StockChangeDetector extends BaseDetector {
    override def detect(oldItem: SomItem, newItem: SomItem): DetectionResult = {
        if (newItem.stock != oldItem.stock) {
            def stockStr(stock: Int): String = stock match {
                case 0 => "Out of stock"
                case -1 => "Infinite stock"
                case n if n > 0 => s"$n items"
            }

            return DetectionResult(
                false, true, Option(
                    SectionBlock.builder().text(PlainTextObject(s"Stock: ${stockStr(oldItem.stock)} -> ${stockStr(newItem.stock)}", true)).build()
                )
            )
        }
        DetectionResult(false, false)
    }
}
