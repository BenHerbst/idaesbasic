package org.idaesbasic.intelline

import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import org.idaesbasic.MainView
import org.idaesbasic.buffer.file.FileModel
import tornadofx.View
import tornadofx.textfield

class IntellineView : View() {
    override val root = textfield () {
        prefWidth = 600.0
        prefHeight = 30.0
        addEventFilter(KeyEvent.KEY_RELEASED) { event ->
            if (event.code == KeyCode.ENTER) {
                // Execute the command
                val commands = text.split(" ")
                when (commands[0]) {
                    "create" -> {
                        when (commands[1]) {
                            "file" -> {
                                // TODO Create controller
                                // Create new blank file
                                val mainView = find(MainView::class)
                                val currentBuffersSize = mainView.controller.buffers.size
                                mainView.newEditor(currentBuffersSize + 1, FileModel(null, null, null))
                                mainView.controller.currentBufferIndexProperty.value = currentBuffersSize
                                mainView.controller.openCurrentBufferIndexBuffer()
                            }
                        }
                    }
                }
                text = ""
            }
        }
    }
}