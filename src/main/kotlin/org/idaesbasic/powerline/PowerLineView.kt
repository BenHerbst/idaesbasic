package org.idaesbasic.powerline

import javafx.beans.property.SimpleStringProperty
import org.idaesbasic.Editor
import org.idaesbasic.MainController
import tornadofx.View
import tornadofx.label
import tornadofx.onChange
import tornadofx.toolbar

class PowerLineView : View() {
    val fileNameProperty = SimpleStringProperty()
    val mainController = find(MainController::class)

    override val root = toolbar() {
        // Listen changes
        // On current buffer got replaced
        mainController.buffers.onChange {
            it.next()
            if(it.wasReplaced()) {
                setFileName()
            }
        }
        // On buffer switched
        mainController.currentBufferIndexProperty.onChange {
            setFileName()
        }
        label(fileNameProperty)
    }

    fun setFileName() {
        if (mainController.getCurrentBuffer() is Editor) {
            fileNameProperty.value = (mainController.getCurrentBuffer() as Editor).fileObject.name
        }
        else {
            fileNameProperty.value = "No file opened"
        }
    }

}