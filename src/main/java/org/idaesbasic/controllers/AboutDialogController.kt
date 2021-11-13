package org.idaesbasic.controllers

import javafx.event.ActionEvent
import javafx.fxml.FXML
import java.awt.Desktop
import java.net.URI

class AboutDialogController {

    @FXML
    fun linkClicked(event: ActionEvent) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI("http://github.com/BenHerbst/idaesbasic"))
        }
    }

}
