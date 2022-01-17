package org.idaesbasic

import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import tornadofx.reloadStylesheetsOnFocus

class IdaesbasicApp : App(MainView::class, MainStyle::class) {
    init {
        reloadStylesheetsOnFocus()
    }

    override fun start(stage: Stage) {
        with(stage) {
            width = 1000.0
            height = 800.0
        }

        super.start(stage)
    }
}

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        launch<IdaesbasicApp>(args)
    }
}