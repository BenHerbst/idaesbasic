package org.idaesbasic

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import org.fxmisc.richtext.CodeArea
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import java.io.File

class MainView: View() {
    val controller: MainController by inject()
    private val textProperty = SimpleStringProperty()

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
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-caret-right"
                        iconColor = Color.web("#f8f8f2")
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-plus"
                        iconColor = Color.web("#f8f8f2")
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    action {
                        showSaveDialogAndSaveText(arrayOf(FileChooser.ExtensionFilter("Plain text", "*.txt")), textProperty.value)
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
                pane() {
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
                pane() {
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
        center {
            val textEdit = CodeArea()
            textProperty.bind(textEdit.textProperty())
            textEdit.padding = Insets(20.0, 20.0, 20.0, 20.0)
            add(textEdit)
        }
    }

    private fun showSaveDialogAndSaveText(extensions: Array<FileChooser.ExtensionFilter>, text: String) {
        val fileArray = chooseFile(
            "Save file",
            extensions,
            null,
            FileChooserMode.Save
        )
        if(fileArray.isNotEmpty()) {
            val file = fileArray[0]
            file.writeText(text)
        }
    }

}

class MainController: Controller() {

    fun saveTextToFile(text: String, file: File) {
        file.writeText(text)
    }
}