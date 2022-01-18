package org.idaesbasic

import javafx.geometry.Insets
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.FileChooser
import org.fxmisc.richtext.CodeArea
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import java.io.File

class MainView : View() {
    val controller: MainController by inject()

    override val root = borderpane {
        top {
            toolbar {
                // Left side
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-caret-left"
                        iconColor = Color.web("#f8f8f2")
                    }
                    action {
                        if (controller.currentBufferIndex > 0) {
                            controller.currentBufferIndex -= 1
                            controller.openCurrentBuffer()
                        }
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-caret-right"
                        iconColor = Color.web("#f8f8f2")
                    }
                    action {
                        if (controller.currentBufferIndex +1 < controller.buffers.size) {
                            controller.currentBufferIndex += 1
                            controller.openCurrentBuffer()
                        }
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    action {
                        newEditor()
                    }
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-plus"
                        iconColor = Color.web("#f8f8f2")
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    action {
                        showSaveDialogAndSaveText(
                            arrayOf(FileChooser.ExtensionFilter("Plain text", "*.txt")),
                            controller.getCurrentBuffer().root.text
                        )
                    }
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-save"
                        iconColor = Color.web("#f8f8f2")
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-file"
                        iconColor = Color.web("#f8f8f2")
                    }
                }
                // Space
                pane {
                    hboxConstraints {
                        hgrow = Priority.SOMETIMES
                    }
                }
                // Center
                textfield {
                    prefWidth = 600.0
                    prefHeight = 30.0
                }
                // Space
                pane {
                    hboxConstraints {
                        hgrow = Priority.SOMETIMES
                    }
                }
                // Right side
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                }
            }
        }
    }

    private fun showSaveDialogAndSaveText(extensions: Array<FileChooser.ExtensionFilter>, text: String) {
        val fileArray = chooseFile(
            "Save file",
            extensions,
            null,
            FileChooserMode.Save
        )
        if (fileArray.isNotEmpty()) {
            val file = fileArray[0]
            file.writeText(text)
        }
    }

    private fun newEditor() {
        val textEditor = find<TextEditor>()
        controller.buffers = controller.buffers.plus(textEditor)
        controller.currentBufferIndex += 1
        controller.openCurrentBuffer()
    }

    fun switchToEditor(textEditor: TextEditor) {
        root.center = textEditor.root
    }
}

class TextEditor: Fragment() {
    override val root = CodeArea()

    init {
        root.padding = Insets(20.0, 20.0, 20.0, 20.0)
    }
}

class MainController : Controller() {

    var buffers = emptyArray<TextEditor>()
    var currentBufferIndex = -1

    fun getCurrentBuffer(): TextEditor {
        return buffers[currentBufferIndex]
    }
    fun openCurrentBuffer() {
        find(MainView::class).switchToEditor(getCurrentBuffer())
    }

    fun saveTextToFile(text: String, file: File) {
        file.writeText(text)
    }
}