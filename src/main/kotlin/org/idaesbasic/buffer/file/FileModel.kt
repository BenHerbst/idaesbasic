package org.idaesbasic.buffer.file

import java.nio.file.Path

class FileModel(filename: String?, filedirectory: Path?, text: String?) {
    var text: String = ""
    var directory: Path? = null
    var name: String? = null

    init {
        if (text != null) {
            this.text = text
        }
        if (filename != null) {
            this.name = filename
        }
        if (filedirectory != null) {
            this.directory = filedirectory
        }
    }
}