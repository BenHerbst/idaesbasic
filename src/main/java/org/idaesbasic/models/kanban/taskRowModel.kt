package org.idaesbasic.models.kanban

import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections

class taskRowModel(val nameValue: String) {

    private var name: StringProperty = SimpleStringProperty()
    private val tasks: ListProperty<kanbanTaskModel> = SimpleListProperty<kanbanTaskModel>(FXCollections.observableArrayList())

    public fun getName(): String? {
        return name.value
    }

    public fun setName(value: String) {
        name.value = value
    }

    public fun nameProperty(): StringProperty {
        return name
    }

    public fun getTasks(): List<kanbanTaskModel> {
        return tasks.value
    }

    init {
        name.set(nameValue)
    }

}