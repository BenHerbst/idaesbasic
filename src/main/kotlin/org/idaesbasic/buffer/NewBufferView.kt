package org.idaesbasic.buffer

import javafx.beans.property.SimpleStringProperty
import org.idaesbasic.MainView
import javafx.stage.FileChooser
import org.idaesbasic.buffer.file.FileModel
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Paths

class NewBufferView : Fragment () {
    val loadDirectory = SimpleStringProperty()
    val loadFile = SimpleStringProperty()
    val controller = find(NewBufferController::class)

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
                            textfield (loadDirectory)
                        }
                    }
                    field ("File name") {
                        hbox {
                            textfield (loadFile)
                        }
                    }
                    button ("Auto pick") {
                        action {
                            val extentions = arrayOf(
                                FileChooser.ExtensionFilter("All", "*"),
                                FileChooser.ExtensionFilter("Plain text", "*.txt"),
                                FileChooser.ExtensionFilter("Java class", "*.java"),
                                FileChooser.ExtensionFilter("Python", "*.py"),
                                FileChooser.ExtensionFilter("Kotlin class", "*.kt"),
                            )
                            val fileArray = chooseFile(
                                "Save file",
                                extentions,
                                null,
                                null,
                                FileChooserMode.Single
                            )
                            if (fileArray.isNotEmpty()) {
                                loadDirectory.set(fileArray[0].path.replace(fileArray[0].name, ""))
                                loadFile.set(fileArray[0].name)
                            }
                        }
                    }
                }
                button("Load file") {
                    action {
                        controller.loadFileInEditor(loadDirectory.value + loadFile.value)
                    }
                }
            }
        }
    }
}

class NewBufferController : Controller () {
    fun loadFileInEditor(location: String) {
        val mainView = find(MainView::class)
        val openedFile = Paths.get(location)
        val file = FileModel(openedFile.fileName.toString(), openedFile, Files.readString(openedFile))
        mainView.newEditor(mainView.controller.currentBufferIndex, file)
        mainView.controller.openCurrentBufferIndexBuffer()
    }
}