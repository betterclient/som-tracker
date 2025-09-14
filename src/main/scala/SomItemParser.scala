package io.github.betterclient.tracker

import org.jsoup.nodes.Element
import upickle.{ReadWriter, macroRW}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object SomItemParser {
    private def parseItem(reg: String, e: Element): RegionlessSomItem = {
        val itemName = e.select(".text-xl.font-bold.mb-2").get(0).text()
        val itemPrice = Option(e.selectFirst("picture.inline-block.mx-1.mb-2.w-5.h-5.mr-2")).flatMap(p => Option(p.nextSibling)).map(_.toString.trim).map(_.toInt).getOrElse(-1)
        val itemDescription = e.select(".text-gray-700").text()
        val itemImage = e.select("img.rounded-lg.w-full.h-auto.object-scale-down.aspect-square.max-h-48").attr("src")
        val itemStock = Option(e.select("p.text-sm.text-orange-600.font-semibold.mt-2").first()).map(_.text()).map(i => i.substring(0, i.indexOf(' ')).toInt).getOrElse(-1)
        val itemID = e.id().substring(e.id().indexOf("-") + 1)

        RegionlessSomItem(
            reg, itemID, itemName, itemDescription, itemPrice, itemStock, itemImage
        )
    }

    private def parseRegion(region: String): List[RegionlessSomItem] =
        val r = request(region).body()
        r.select("[id^=item-]").toArray(Array[Element]()).map(parseItem(region, _)).toList

    def parseAll(): ListBuffer[SomItem] = {
        val regionlessItems = List(
            parseRegion("US"), parseRegion("EU"), parseRegion("IN"),
            parseRegion("CA"), parseRegion("AU"), parseRegion("XX")
        )

        val regionItems = ListBuffer[(String, String, String, mutable.HashMap[String, Int], Int, String)]()
        regionlessItems.flatten.foreach(item => {
            if (regionItems.exists(_._1 == item.id)) {
                //just add it to the existing one
                regionItems.find(_._1 == item.id).get._4(item.region) = item.price
            } else {
                //new
                regionItems.addOne(
                    (item.id, item.name, item.description, mutable.HashMap((item.region, item.price)), item.stock, item.image)
                )
            }
        })

        regionItems.map(item => {
            SomItem(
                item._1.toInt,
                item._2,
                item._3,
                item._4.getOrElse("US", -1),
                item._4.getOrElse("EU", -1),
                item._4.getOrElse("IN", -1),
                item._4.getOrElse("CA", -1),
                item._4.getOrElse("AU", -1),
                item._4.getOrElse("XX", -1),
                item._5,
                item._6
            )
        })
    }
}

case class RegionlessSomItem
(
    region: String,
    id: String,
    name: String,
    description: String,
    price: Int,
    stock: Int,
    image: String,
)

implicit val itemRW: ReadWriter[SomItem] = macroRW
case class SomItem
(
  id: Int,
  name: String,
  description: String,
  priceUS: Int,
  priceEU: Int,
  priceIN: Int,
  priceCA: Int,
  priceAU: Int,
  priceXX: Int,
  stock: Int,
  image: String,
)