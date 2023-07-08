package org.idaesbasic

import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class IdaesbasicApp : App(MainView::class, MainStyle::class) {

    override fun start(stage: Stage) {
        stage.apply {
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
