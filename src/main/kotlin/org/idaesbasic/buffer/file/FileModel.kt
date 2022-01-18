package org.idaesbasic.buffer.file

import java.nio.file.Path

class FileModel(filename: String?, filedirectory: Path?, text: String?) {
    var text: String = ""
    init {
        if (text != null) {
            this.text = text
        }
    }
}