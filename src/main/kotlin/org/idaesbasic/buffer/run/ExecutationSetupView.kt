package org.idaesbasic.buffer.run

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ExecutationSetupView : View() {
    override val root = squeezebox {
        val controller = find(RunConfigController::class)
        fold ("Existing configs") {
            listview (controller.configs) {
                isEditable = true
                cellFragment(RunConfigFragment::class)
            }
        }
        fold ("New config") {
            val configName = SimpleStringProperty()
            val configCommand = SimpleStringProperty()

            form {
                fieldset ("Information") {
                    field ("Name of config") {
                        textfield (configName)
                    }
                    field ("Command") {
                        textfield (configCommand)
                    }
                }
                button ("Create config") {
                    action {
                        // Creates the config with the form fields data
                        controller.addConfig(RunConfigModel(configName.value, configCommand.value))
                        configName.set("")
                    }
                }
            }
        }
    }
}