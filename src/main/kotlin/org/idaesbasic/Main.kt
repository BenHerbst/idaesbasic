package org.idaesbasic

import javafx.stage.Stage
import kotlin.jvm.JvmStatic
import tornadofx.*

class IdaesbasicApp: App(MainView::class) {
    init {
        reloadViewsOnFocus()
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