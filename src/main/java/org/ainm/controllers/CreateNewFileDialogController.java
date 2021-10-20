package org.ainm.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateNewFileDialogController {

    @FXML
    private TextField directoryField;

    @FXML
    private TextField filenameField;

    @FXML
    private Label extentionLabel;

    String getDirectory() {
        return directoryField.getText();
    }

    String getFilename() {
        return filenameField.getText();
    }

    void changeExtention(String value) {
        extentionLabel.setText(value);
    }
}