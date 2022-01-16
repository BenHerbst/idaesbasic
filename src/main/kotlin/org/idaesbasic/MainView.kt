package org.idaesbasic

import javafx.scene.text.Text
import org.fxmisc.richtext.InlineCssTextArea
import tornadofx.*

class MainView: View() {
    override val root = borderpane {
        top<TopView>()
        center<CenterView>()
    }
}

class TopView: View() {
    override val root = hbox {
        textfield {  }
        button {  }
    }
}

class CenterView: View() {
    override val root = hbox {
        val myCustomComponent = InlineCssTextArea()
        add(myCustomComponent)
        myCustomComponent.appendText("This is a test")
    }
}
