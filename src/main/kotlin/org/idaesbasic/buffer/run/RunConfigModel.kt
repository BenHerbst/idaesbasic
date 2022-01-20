package org.idaesbasic.buffer.run

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel

class RunConfigModel (name: String,  runCommand: String?) {
    var nameProperty = SimpleStringProperty(name)
    var commandProperty = SimpleStringProperty(runCommand)
}

class RunConfigProperty(property: ObjectProperty<RunConfigModel>) : ItemViewModel<RunConfigModel>(itemProperty = property) {
    val name = bind(autocommit = true) { item?.nameProperty }
}