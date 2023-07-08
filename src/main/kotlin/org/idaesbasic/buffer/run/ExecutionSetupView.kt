package org.idaesbasic.buffer.run

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ExecutionSetupView : View() {
    private val controller: RunConfigController by inject()

    private val configName = SimpleStringProperty()
    private val configCommand = SimpleStringProperty()

    override val root = squeezebox {
        fold("Existing configs") {
            listview(controller.configs) {
                isEditable = true
                cellFragment(RunConfigFragment::class)
            }
        }
        fold("New config") {
            form {
                fieldset("Information") {
                    field("Name of config") {
                        textfield(configName)
                    }
                    field("Command") {
                        textfield(configCommand)
                    }
                }
                button("Create config") {
                    action {
                        controller.addConfig(RunConfigModel(configName.value, configCommand.value))
                        configName.set("")
                        configCommand.set("")
                    }
                }
            }
        }
    }
}
