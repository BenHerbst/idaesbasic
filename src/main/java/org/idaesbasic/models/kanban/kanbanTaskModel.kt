package org.idaesbasic.models.kanban

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class kanbanTaskModel(taskValue: String, dateValue: String) {
    private val task: StringProperty = SimpleStringProperty()
    private val date: StringProperty = SimpleStringProperty()

    init {
        task.set(taskValue)
        date.set(dateValue)
    }

    public fun getTask(): String {
        return task.value
    }
}