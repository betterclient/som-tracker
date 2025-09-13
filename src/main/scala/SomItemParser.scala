package io.github.betterclient.tracker

import org.jsoup.nodes.{Element, Node}

object SomItemParser {
    def parseItem(e: Element): Unit = {
        val itemName = e.select(".text-xl.font-bold.mb-2").get(0).text()
        val itemPrice = e.selectFirst("picture.inline-block.mx-1.mb-2.w-5.h-5.mr-2").nextSibling.toString.trim
        val itemDescription = e.select(".text-gray-700").text()
        
        
    }

    def parseAll(): Unit =
        val r = request().body()
        removeComments(r)
        r.select("[id^=item-]").forEach(parseItem(_))

    def removeComments(node: Node): Unit = {
        node.childNodes.stream.filter((n) => ("#comment" == n.nodeName || "script" == n.nodeName)).forEach(_.remove())
        node.childNodes.forEach(this.removeComments)
    }
}
