package org.idaesbasic.powerline

import javafx.beans.property.SimpleStringProperty
import org.idaesbasic.Editor
import org.idaesbasic.MainController
import tornadofx.View
import tornadofx.label
import tornadofx.onChange
import tornadofx.toolbar

class PowerLineView : View() {
    private val fileNameProperty = SimpleStringProperty()
    private val mainController: MainController by inject()

    override val root = toolbar {
        // Listen to changes
        // On current buffer got replaced
        mainController.buffers.onChange { change ->
            change.next()
            if (change.wasReplaced()) {
                setFileName()
            }
        }
        // On buffer switched
        mainController.currentBufferIndexProperty.onChange { _ ->
            setFileName()
        }
        label(fileNameProperty)
    }

    private fun setFileName() {
        val currentBuffer = mainController.getCurrentBuffer()
        fileNameProperty.value = if (currentBuffer is Editor) {
            currentBuffer.fileObject.name
        } else {
            "No file opened"
        }
    }
}
