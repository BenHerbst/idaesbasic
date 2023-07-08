package org.idaesbasic

import javafx.application.Platform
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.Event
import javafx.geometry.Insets
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.wellbehaved.event.EventPattern.anyOf
import org.fxmisc.wellbehaved.event.EventPattern.keyPressed
import org.fxmisc.wellbehaved.event.InputMap
import org.fxmisc.wellbehaved.event.Nodes
import org.idaesbasic.buffer.NewBufferView
import org.idaesbasic.buffer.file.FileModel
import org.idaesbasic.buffer.run.ExecutionSetupView
import org.idaesbasic.buffer.run.RunConfigController
import org.idaesbasic.intelline.IntellineView
import org.idaesbasic.powerline.PowerLineView
import org.idaesbasic.sidepanel.SidepanelView
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher
import java.util.regex.Pattern

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
                        if (controller.currentBufferIndexProperty.value + 1 < controller.buffers.size) {
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
                            extensions = arrayOf(
                                FileChooser.ExtensionFilter("All", "*"),
                                FileChooser.ExtensionFilter("Plain text", "*.txt"),
                                FileChooser.ExtensionFilter("Java class", "*.java"),
                                FileChooser.ExtensionFilter("Python", "*.py"),
                                FileChooser.ExtensionFilter("Kotlin class", "*.kt")
                            ),
                            text = currentEditor.root.text,
                            file = currentEditor.fileObject
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
                        item("Change config") {
                            action {
                                ExecutionSetupView().openWindow()
                            }
                        }
                    }
                    val runContextToggleGroup = togglegroup { }
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
                title = "Save file",
                filters = extensions,
                mode = FileChooserMode.Save
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
        controller.currentBufferIndexProperty.set(controller.buffers.size - 1)
        controller.openCurrentBufferIndexBuffer()
    }

    fun switchCenterToBufferView(bufferView: Fragment) {
        root.center = bufferView.root
    }
}

class MainViewModel : ItemViewModel<MainView>() {
    val root = bind(MainView::root)
}

class Editor(file: FileModel) : Fragment() {
    override val root = CodeArea()
    lateinit var fileObject: FileModel

    init {
        root.setParagraphGraphicFactory(LineNumberFactory.get(root))
        fileObject = file
        root.padding = Insets(20.0, 20.0, 20.0, 20.0)
        root.appendText(file.text)
        val preventTab: InputMap<Event> = InputMap.consume(
            anyOf(
                // Prevent tab to replace with 4 spaces indentation
                keyPressed(KeyCode.TAB)
            )
        )
        Nodes.addInputMap(root, preventTab)
        val whiteSpace: Pattern = Pattern.compile("^\\s+")
        root.addEventHandler(KeyEvent.KEY_PRESSED) { KE ->
            // Vim movement
            if (KE.code === KeyCode.H) {
                Platform.runLater {
                    val caretPosition: Int = root.caretPosition
                    root.deleteText(caretPosition - 1, caretPosition)
                    root.moveTo(caretPosition - 2)
                }
            }
            if (KE.code === KeyCode.L) {
                Platform.runLater {
                    val caretPosition: Int = root.caretPosition
                    root.deleteText(caretPosition - 1, caretPosition)
                    root.moveTo(caretPosition)
                }
            }
            // Four spaces for tab
            if (KE.code === KeyCode.TAB) {
                val caretPosition: Int = root.caretPosition
                root.insertText(caretPosition, "    ")
            }
            // Auto-indent after pressing enter from the last line
            if (KE.code === KeyCode.ENTER) {
                val caretPosition: Int = root.caretPosition
                val currentParagraph: Int = root.currentParagraph
                val m0: Matcher = whiteSpace.matcher(root.getParagraph(currentParagraph - 1).getSegments()[0])
                if (m0.find()) Platform.runLater { root.insertText(caretPosition, m0.group()) }
            }
            //TODO: Implement backspaces that remove 4 space indentations
        }
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
