package org.idaesbasic.buffer.run

import tornadofx.Fragment
import tornadofx.ListCellFragment
import tornadofx.hbox
import tornadofx.label

class RunConfigFragment : ListCellFragment<RunConfigModel>() {
    val runConfigModel = RunConfigProperty(itemProperty)

    override val root = hbox {
        label (runConfigModel.name) {  }
    }
}