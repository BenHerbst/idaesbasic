package org.idaesbasic.controllers.kanban

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.idaesbasic.controllers.todolist.CreateNewTodoController
import org.idaesbasic.controllers.todolist.TodoitemController
import org.idaesbasic.models.TaskRowModel
import java.io.IOException


class TaskRowController {
    @FXML
    private lateinit var todoContainer: VBox

    @FXML
    private lateinit var title: Label

    @FXML
    private lateinit var taskRow: VBox;

    @JvmField
    var taskModel: TaskRowModel? = TaskRowModel()

    fun getTodoContainer(): VBox {
        return todoContainer
    }

    fun initialize() {
        title?.textProperty()?.bindBidirectional(taskModel?.titleProperty())
        taskRow.userData = this;
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
            val todoItem: HBox = loader.load<Node>() as HBox
            val todoItemController: TodoitemController? = loader.getController<TodoitemController>();
            val newTaskDialogController: CreateNewTodoController? =
                newTaskLoader.getController<CreateNewTodoController>()
            todoItemController?.setTodo(newTaskDialogController?.todo)
            todoItemController?.setDate(newTaskDialogController?.date)
            // Set todoItem controller propertie
            todoItem.userData = loader.getController()
            println(todoItem.userData)
            // Add todo to todolist
            todoContainer!!.children.add(todoItem)
        }
    }

    @FXML
    fun dragOverAction(event: DragEvent) {
        event.acceptTransferModes(TransferMode.MOVE)
    }

    @FXML
    fun dragDroppedAction(event: DragEvent) {
        val db: Dragboard = event.dragboard
        taskRow.parent.childrenUnmodifiable.forEach{ child ->
            run {
                child as VBox
                val todoContainer = (child.userData as TaskRowController).getTodoContainer()
                todoContainer.children.forEach{
                    task ->
                    run {
                        if(db.string == (task.userData as TodoitemController).draggingID) {
                            todoContainer.children.remove(task)
                            return
                        }
                    }
                }
            }
        }
    }
}