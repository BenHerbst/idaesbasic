package org.idaesbasic.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public final void deleteCurrentFile() throws IOException {
        Files.delete(getOpenedFile());
    }
    
}