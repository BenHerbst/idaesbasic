package org.idaesbasic.models;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ViewModel {

    private final ObjectProperty<Path> openedFile = new SimpleObjectProperty<Path>();

    public final ObjectProperty<Path> openedFileProperty() {
        return this.openedFile;
    }
    

    public final Path getOpenedFile() {
        return this.openedFileProperty().get();
    }
    

    public final void setOpenedFile(final Path openedFile) {
        this.openedFileProperty().set(openedFile);
    }
    
}