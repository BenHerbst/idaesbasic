package org.idaesbasic.buffer

import org.idaesbasic.MainView
import tornadofx.*

class NewBufferView : Fragment () {
    override val root = squeezebox {
        fold("New file", expanded = true) {
            form {
                fieldset ("Basic file configuration"){
                    field("Filename") {
                        textfield()
                    }
                    field("Language") {
                        text("Plain text")
                    }
                }
                fieldset ("Advanced file configuration") {

                }
                button("Create") {
                    action {
                        val mainView = find(MainView::class)
                        mainView.newEditor(mainView.controller.currentBufferIndex)
                        mainView.controller.openCurrentBufferIndexBuffer()
                    }
                }
            }
        }
    }
}