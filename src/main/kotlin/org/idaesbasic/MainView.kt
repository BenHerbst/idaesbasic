package org.idaesbasic

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import org.fxmisc.richtext.CodeArea
import org.idaesbasic.buffer.NewBufferView
import org.idaesbasic.buffer.file.FileModel
import org.idaesbasic.buffer.run.ExecutationSetupView
import org.idaesbasic.buffer.run.RunConfigController
import org.idaesbasic.buffer.run.RunConfigModel
import org.idaesbasic.buffer.run.RunConfigProperty
import org.idaesbasic.intelline.IntellineView
import org.idaesbasic.powerline.PowerLineView
import org.idaesbasic.sidepanel.SidepanelView
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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
                        if (controller.currentBufferIndexProperty.get() > 0) {
                            controller.currentBufferIndexProperty.value -= 1
                            controller.openCurrentBufferIndexBuffer()
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
                        if (controller.currentBufferIndexProperty.value +1 < controller.buffers.size) {
                            controller.currentBufferIndexProperty.value += 1
                            controller.openCurrentBufferIndexBuffer()
                        }
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    action {
                        newBuffer()
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
                        controller.removeBuffer(controller.currentBufferIndexProperty.get())
                    }
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-minus"
                        iconColor = Color.web("#f8f8f2")
                    }
                }
                button {
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    action {
                        val currentEditor: Editor = controller.getCurrentBuffer() as Editor
                        showSaveDialogAndSaveText(
                            arrayOf(
                                FileChooser.ExtensionFilter("All", "*"),
                                FileChooser.ExtensionFilter("Plain text", "*.txt"),
                                FileChooser.ExtensionFilter("Java class", "*.java"),
                                FileChooser.ExtensionFilter("Python", "*.py"),
                                FileChooser.ExtensionFilter("Kotlin class", "*.kt"),
                                ),
                            currentEditor.root.text,
                            currentEditor.fileObject
                        )
                    }
                    graphic = FontIcon().apply {
                        iconLiteral = "fa-save"
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
                add(IntellineView().root)
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
                    val configsController = find(RunConfigController::class)
                    val contextMenu = contextmenu {
                        item ("Change config"){
                            action {
                                ExecutationSetupView().openWindow()
                            }
                        }
                    }
                    val runContextToggleGroup = togglegroup {  }
                    configsController.configs.onChange() {
                        // Update context menu to the changed config
                        it.next()
                        if (it.wasAdded()) {
                            val config = configsController.configs[it.to - 1]
                            contextMenu.radiomenuitem(config.nameProperty.value, runContextToggleGroup) {
                                action {
                                    check(true)
                                    configsController.currentConfig = config
                                    configsController.runCurrentConfig()
                                }
                            }
                        }
                    }
                    action {
                        configsController.runCurrentConfig()
                    }
                }
                checkbox {
                    val sidepanelView = SidepanelView()
                    prefWidth = 30.0
                    prefHeight = prefWidth
                    action {
                        if (isSelected) {
                            // Show sidepanel
                            sidepanelView.openWindow()?.apply {
                                this.width = 400.0
                                this.height = 700.0
                                this.x = currentStage?.x!! + (currentStage?.width!! - this.width - 50.0)
                                this.y = currentStage?.y!! + (currentStage?.height!! - this.height) / 2
                                this.isAlwaysOnTop = true
                            }
                        } else {
                            // Hide sidepanel
                            sidepanelView.currentWindow?.hide()
                        }
                    }
                }
            }
        }
        bottom<PowerLineView>()
    }

    private fun showSaveDialogAndSaveText(extensions: Array<FileChooser.ExtensionFilter>, text: String, file: FileModel) {
        if (file.directory == null) {
            val fileArray = chooseFile(
                "Save file",
                extensions,
                null,
                null,
                FileChooserMode.Save
            )
            if (fileArray.isNotEmpty()) {
                val newDirectory = fileArray[0]
                file.directory = Paths.get(newDirectory.path)
            }
        }
        if (file.directory != null) {
            controller.saveTextToFile(text, file.directory!!)
        }
    }

    fun newEditor(bufferIndex: Int, file: FileModel) {
        val textEditor = Editor(file)
        if (controller.buffers.size >= bufferIndex) {
            // Replace the given buffer index
            controller.buffers[bufferIndex] = textEditor
        } else {
            // Add as new buffer
            controller.buffers.add(textEditor)
        }
    }

    fun newBuffer() {
        val newBuffer = NewBufferView()
        controller.buffers.add(newBuffer)
        controller.currentBufferIndexProperty.set(controller.buffers.size -1)
        controller.openCurrentBufferIndexBuffer()
    }

    fun switchCenterToBufferView(bufferView: Fragment) {
        root.center = bufferView.root
    }
}

class MainViewModel : ItemViewModel<MainView>() {
    val root = bind(MainView::root)
}

class Editor(file: FileModel): Fragment() {
    override val root = CodeArea()
    lateinit var fileObject: FileModel

    init {
        fileObject = file
        root.padding = Insets(20.0, 20.0, 20.0, 20.0)
        root.appendText(file.text)
    }
}

class MainController : Controller() {

    var buffers = SortedFilteredList<Fragment>()
    var currentBufferIndexProperty = SimpleIntegerProperty(-1)

    fun getCurrentBuffer(): Fragment {
        return buffers[currentBufferIndexProperty.get()]
    }
    fun openCurrentBufferIndexBuffer() {
        find(MainView::class).switchCenterToBufferView(getCurrentBuffer())
    }

    fun removeBuffer(index: Int) {
        buffers.removeAt(index)
        if (currentBufferIndexProperty.get() >= index && currentBufferIndexProperty.get() != 0) {
            currentBufferIndexProperty.value -= 1
        }
        openCurrentBufferIndexBuffer()
    }

    fun saveTextToFile(text: String, file: Path) {
        Files.writeString(file, text)
    }

}