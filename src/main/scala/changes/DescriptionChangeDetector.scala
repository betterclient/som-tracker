package io.github.betterclient.tracker
package changes

import html.SomItem

import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.PlainTextObject

object DescriptionChangeDetector extends BaseDetector {
    override def detect(oldItem: SomItem, newItem: SomItem): DetectionResult = {
        if (newItem.description != oldItem.description) {
            DetectionResult(
                false, true, Option(
                    SectionBlock.builder().text(
                        PlainTextObject(
                            s"${oldItem.description} -> ${newItem.description}",
                            true
                        )
                    ).build()
                )
            )
        } else {
            DetectionResult(
                false, false, Option(
                    SectionBlock.builder().text(PlainTextObject(newItem.description, true)).build()
                )
            )
        }
    }
}
