package org.idaesbasic.controllers.kanban

import javafx.fxml.Initializable
import javafx.fxml.FXML
import javafx.scene.layout.VBox
import org.idaesbasic.models.TaskRowModel
import java.util.ResourceBundle
import kotlin.Throws
import java.io.IOException
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.DialogPane
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import org.idaesbasic.controllers.todolist.CreateNewTodoController
import org.idaesbasic.controllers.todolist.TodoitemController
import java.net.URL
import java.util.Optional

class TaskRowController {
    @FXML
    private lateinit var todoContainer: VBox

    @FXML
    private lateinit var title: Label

    @FXML
    private lateinit var taskRow: VBox;

    @JvmField
    var taskModel: TaskRowModel? = TaskRowModel()

    fun initialize() {
        title?.textProperty()?.bindBidirectional(taskModel?.titleProperty())
    }

    @FXML
    fun deleteThisRow() {
        (taskRow.parent as HBox).children.remove(taskRow)
    }

    @FXML
    @Throws(IOException::class)
    fun addNewTask() {
        val newTaskLoader = FXMLLoader()
        newTaskLoader.location = javaClass.getResource("/fxml/dialogs/AddTodo.fxml")
        val newTaskDialogPane = newTaskLoader.load<DialogPane>()
        val newTaskDialog = Dialog<ButtonType>()
        newTaskDialog.dialogPane = newTaskDialogPane
        val result = newTaskDialog.showAndWait()
        if (result.get() == ButtonType.FINISH) {
            // Load a new todo item
            val loader = FXMLLoader()
            loader.location = javaClass.getResource("/fxml/views/todo/todo_item.fxml")
            val todoItem = loader.load<Node>()
            val todoItemController: TodoitemController? = loader.getController<TodoitemController>();
            val newTaskDialogController: CreateNewTodoController? = newTaskLoader.getController<CreateNewTodoController>()
            todoItemController?.setTodo(newTaskDialogController?.todo)
            todoItemController?.setDate(newTaskDialogController?.date)
            // Add todo to todolist
            todoContainer!!.children.add(todoItem)
        }
    }
}