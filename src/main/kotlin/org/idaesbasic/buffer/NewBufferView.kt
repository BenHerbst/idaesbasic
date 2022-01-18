package org.idaesbasic.buffer

import org.idaesbasic.MainView
import org.idaesbasic.buffer.file.FileModel
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
                        val file = FileModel(null, null, null)
                        mainView.newEditor(mainView.controller.currentBufferIndex, file)
                        mainView.controller.openCurrentBufferIndexBuffer()
                    }
                }
            }
        }
        fold("Load file", expanded = true) {
            form {
                fieldset ("Location") {
                    field ("Directory") {
                        hbox {
                            textfield { }
                            button {  }
                        }
                    }
                }
                button("Load file") {
                    action {
                        val mainView = find(MainView::class)
                        val file = FileModel(null, null, null)
                        mainView.newEditor(mainView.controller.currentBufferIndex, file)
                        mainView.controller.openCurrentBufferIndexBuffer()
                    }
                }
            }
        }
    }
}