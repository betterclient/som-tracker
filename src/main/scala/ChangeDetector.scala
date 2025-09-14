package io.github.betterclient.tracker

import java.io.FileOutputStream
import scala.util.Using
import upickle.default.write

object ChangeDetector {
    def detectChanges(): Unit = {
        Using(FileOutputStream("itemsNew.json")) {
            _.write(write(SomItemParser.parseAll().toList).getBytes)
        }
    }
}
