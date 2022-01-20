package org.idaesbasic.buffer.run

import tornadofx.Controller
import tornadofx.SortedFilteredList

class RunConfigController : Controller() {
    val configs = SortedFilteredList<RunConfigModel>()
    var currentConfig: RunConfigModel? = null

    fun addConfig(config: RunConfigModel) {
        // Add a new run config
        configs.add(config)
    }

    fun runCurrentConfig() {
        currentConfig?.let { run(it) }
    }

    fun run(config: RunConfigModel) {
        // Executes the given config by command
        Runtime.getRuntime().exec(config.commandProperty.value)
    }
}