package org.idaesbasic.controllers

import javafx.fxml.FXML
import javafx.scene.control.TextField

class CreateNewDirectoryDialogController {

    @FXML
    lateinit var directoryNameTextfield: TextField;

    fun getDirectoryName(): String {
        return directoryNameTextfield.text
    }

}