package io.github.betterclient.tracker
package changes

import html.SomItem

import com.slack.api.model.block.LayoutBlock

val detectors = List(
    NameChangeDetector,
    DescriptionChangeDetector,
    PriceChangeDetector,
    StockChangeDetector,
)
trait BaseDetector {
    def detect(oldItem: SomItem, newItem: SomItem): DetectionResult
}

case class DetectionResult(ping: Boolean, changed: Boolean, block: Option[LayoutBlock] = Option.empty)