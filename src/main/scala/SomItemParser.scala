package io.github.betterclient.tracker

import org.jsoup.nodes.Element
import upickle.{ReadWriter, macroRW}

import scala.collection.mutable
import scala.collection.mutable.{HashMap, ListBuffer, Map}

object SomItemParser {
    def parseItem(reg: String, e: Element): RegionlessSomItem = {
        val itemName = e.select(".text-xl.font-bold.mb-2").get(0).text()
        val itemPrice = Option(e.selectFirst("picture.inline-block.mx-1.mb-2.w-5.h-5.mr-2")).flatMap(p => Option(p.nextSibling)).map(_.toString.trim).map(_.toInt).getOrElse(-1)
        val itemDescription = e.select(".text-gray-700").text()
        val itemImage = e.select("img.rounded-lg.w-full.h-auto.object-scale-down.aspect-square.max-h-48").attr("src")
        val itemStock = Option(e.select("p.text-sm.text-orange-600.font-semibold.mt-2").first()).map(_.text()).map(i => i.substring(0, i.indexOf(' ')).toInt).getOrElse(-1)

        RegionlessSomItem(
            reg, itemName, itemDescription, itemPrice, itemStock, itemImage
        )
    }

    def parseRegion(region: String): List[RegionlessSomItem] =
        val r = request(region).body()
        r.select("[id^=item-]").toArray(Array[Element]()).map(parseItem(region, _)).toList

    def parseAll(): ListBuffer[RegionSomItem] = {
        val regionlessItems = List(
            parseRegion("US"), parseRegion("EU"), parseRegion("IN"),
            parseRegion("CA"), parseRegion("AU"), parseRegion("XX")
        )

        val regionItems = ListBuffer[RegionSomItem]()
        regionlessItems.flatten.foreach(item => {
            if (regionItems.exists(_.name == item.name)) {
                //just add it to the existing one
                regionItems.find(_.name == item.name).get.price(item.region) = item.price
            } else {
                //new
                regionItems.addOne(
                    RegionSomItem(
                        item.name, item.description, HashMap((item.region, item.price)), item.stock, item.image
                    )
                )
            }
        })
        regionItems
    }
}

case class RegionlessSomItem
(
    region: String,
    name: String,
    description: String,
    price: Int,
    stock: Int,
    image: String,
)

implicit val itemRW: ReadWriter[RegionSomItem] = macroRW
case class RegionSomItem
(
  name: String,
  description: String,
  price: mutable.Map[String, Int],
  stock: Int,
  image: String,
)