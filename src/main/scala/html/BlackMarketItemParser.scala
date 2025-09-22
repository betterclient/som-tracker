package io.github.betterclient.tracker
package html

import org.jsoup.nodes.Element

import java.util.concurrent.atomic.AtomicBoolean
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Try

object BlackMarketItemParser {
    private def parseItem(reg: String, element: Element): RegionlessSomItem = {
        val price = element.attr("data-item-price").toFloat.toInt
        val id = element.attr("data-item-id")
        val image = element.select("img").first().attr("src")
        val title = Option(element.select(".shop-item-title").first())
            .map(_.ownText().trim)
            .getOrElse("")
        val description = Option(element.select(".shop-item-description").first())
            .map(_.ownText().trim)
            .getOrElse("")
        val stock = {
            val remainingStockElement = Option(element.select(".shop-item-remaining").first())
            val outOfStockElement = Option(element.select(".shop-item-out").first())

            if (remainingStockElement.isDefined) {
                val stockText = remainingStockElement.get.text()
                val stockPattern = "(\\d+).*".r
                stockText match {
                    case stockPattern(num) => Try(num.toInt).getOrElse(-1)
                    case _ => -1
                }
            } else if (outOfStockElement.isDefined) {
                0
            } else {
                -1
            }
        }
        
        RegionlessSomItem(
            reg, id, title, description,
            price, stock, image, false, true            
        )
    }

    private def parseRegion(region: String): List[RegionlessSomItem] = {
        val body = requestBlack(region).map(_.body())
        val buffer = ListBuffer[RegionlessSomItem]()
        if(body.isEmpty) return List()
        buffer.addAll(body.get.select(".shop-item-row").asList().asScala.map(parseItem(region, _)))
        buffer.toList
    }

    def parseAll(): ListBuffer[SomItem] = {
        val regionlessItems = List(
            parseRegion("US"), parseRegion("EU"), parseRegion("IN"),
            parseRegion("CA"), parseRegion("AU"), parseRegion("XX")
        )
        if (regionlessItems.exists(_.isEmpty)) return ListBuffer()

        val regionItems = ListBuffer[IntermediateItem]()
        regionlessItems.flatten.foreach(item => {
            if (regionItems.exists(_.id == item.id)) {
                //just add it to the existing one
                val value = regionItems.find(_.id == item.id).get
                value.priceMap(item.region) = item.price
                if (item.hasSale) value.hasSale.set(true)
            } else {
                //new
                regionItems.addOne(
                    IntermediateItem(item.id, item.name, item.description, mutable.HashMap((item.region, item.price)), item.stock, item.image, AtomicBoolean(item.hasSale), true)
                )
            }
        })

        regionItems.map(item => {
            SomItem(
                item.id.toInt,
                item.name,
                item.description,
                item.priceMap.getOrElse("US", -2),
                item.priceMap.getOrElse("EU", -2),
                item.priceMap.getOrElse("IN", -2),
                item.priceMap.getOrElse("CA", -2),
                item.priceMap.getOrElse("AU", -2),
                item.priceMap.getOrElse("XX", -2),
                item.stock,
                item.image,
                item.hasSale.get(),
                true
            )
        })
    }
}