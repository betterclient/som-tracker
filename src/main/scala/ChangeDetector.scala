package io.github.betterclient.tracker

import com.slack.api.methods.MethodsClient

import java.io.{File, FileInputStream, FileOutputStream}
import scala.util.Using
import upickle.default.{read, write}

object ChangeDetector {
    def detectChanges(client: MethodsClient): Unit = {
        val old = read[List[SomItem]](String(Using(FileInputStream("items.json")) { _.readAllBytes() }.get))
        val current = SomItemParser.parseAll().toList

        var ping = false
        var changes = false
        for ((oldItem, newItem) <- old.map(i => (i, current.find(_.id == i.id).orNull))) {
            scala.util.control.Breaks.breakable {
                if(newItem == null) {
                    //deleted!
                    ping = true
                    println(s":deleted: ${oldItem.name}")
                    scala.util.control.Breaks.break()
                }

                detectGeneralChanges(oldItem, newItem, () => { changes = true }, () => { ping = true })
            }
        }

        for(newItem <- current.filter(i => !old.exists(i.id == _.id))) {
            ping = true
            println(s":new: ${newItem.name}")
        }
        if(ping) changes = true

        File("items.json").delete()
        Using(FileOutputStream("items.json")) {
            _.write(write(current, indent = 4).getBytes)
        }
    }

    private def detectGeneralChanges(oldItem: SomItem, newItem: SomItem, changed: () => Unit, ping: () => Unit): Unit = {
        if (newItem.name != oldItem.name) {
            changed()
            println(s"${oldItem.name} -> ${newItem.name}")
        }

        if (newItem.description != oldItem.description) {
            changed()
            println(s"${oldItem.description} -> ${newItem.description}")
        }

        if (newItem.description != oldItem.description) {
            changed()
            println(s"${oldItem.description} -> ${newItem.description}")
        }

        if (newItem.priceUS != oldItem.priceUS) {
            ping()
            println(s"United states: ${oldItem.priceUS} -> ${newItem.priceUS}")
        }

        if (newItem.priceEU != oldItem.priceEU) {
            ping()
            println(s"Europe: ${oldItem.priceEU} -> ${newItem.priceEU}")
        }

        if (newItem.priceIN != oldItem.priceIN) {
            ping()
            println(s"India: ${oldItem.priceIN} -> ${newItem.priceIN}")
        }

        if (newItem.priceCA != oldItem.priceCA) {
            ping()
            println(s"Canada: ${oldItem.priceCA} -> ${newItem.priceCA}")
        }

        if (newItem.priceAU != oldItem.priceAU) {
            ping()
            println(s"∀nsʇɹɐlᴉɐ: ${oldItem.priceAU} -> ${newItem.priceAU}")
        }

        if (newItem.priceXX != oldItem.priceXX) {
            ping()
            println(s"Rest of the world: ${oldItem.priceXX} -> ${newItem.priceXX}")
        }

        if (newItem.stock != oldItem.stock) {
            changed()
            println(s"Stock: ${oldItem.stock} -> ${newItem.stock}")
        }
    }
}