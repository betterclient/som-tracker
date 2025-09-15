package io.github.betterclient.tracker
package changes

import html.SomItem

import com.slack.api.model.block.HeaderBlock
import com.slack.api.model.block.composition.PlainTextObject

object NameChangeDetector extends BaseDetector {
    override def detect(oldItem: SomItem, newItem: SomItem): DetectionResult = {
        if (newItem.name != oldItem.name) {
            DetectionResult(
                false, true, Option(
                    HeaderBlock.builder().text(
                        PlainTextObject(
                            s"${oldItem.name} -> ${newItem.name}", 
                            true
                        )
                    ).build()
                )
            )
        } else {
            DetectionResult(
                false, false, Option(
                    HeaderBlock.builder().text(PlainTextObject(newItem.name, true)).build()
                )
            )
        }
    }
}
