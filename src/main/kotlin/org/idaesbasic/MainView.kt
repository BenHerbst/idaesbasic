package org.idaesbasic

import javafx.geometry.Insets
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.fxmisc.richtext.InlineCssTextArea
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*

class MainView: View() {
    override val root = borderpane {
        top<TopView>()
        center<CenterView>()
    }
}

class TopView: View() {
    override val root = toolbar {
        // Left side
        button {
            prefWidth = 30.0
            prefHeight = prefWidth
            graphic = FontIcon().apply {
                iconLiteral = "fa-caret-left"
                iconColor = Color.web("#f8f8f2")
            }
        }
        button {
            prefWidth = 30.0
            prefHeight = prefWidth
            graphic = FontIcon().apply {
                iconLiteral = "fa-caret-right"
                iconColor = Color.web("#f8f8f2")
            }
        }
        button {
            prefWidth = 30.0
            prefHeight = prefWidth
            graphic = FontIcon().apply {
                iconLiteral = "fa-plus"
                iconColor = Color.web("#f8f8f2")
            }
        }
        button {
            prefWidth = 30.0
            prefHeight = prefWidth
            graphic = FontIcon().apply {
                iconLiteral = "fa-save"
                iconColor = Color.web("#f8f8f2")
            }
        }
        button {
            prefWidth = 30.0
            prefHeight = prefWidth
            graphic = FontIcon().apply {
                iconLiteral = "fa-file"
                iconColor = Color.web("#f8f8f2")
            }
        }
        // Space
        pane() {
            hboxConstraints {
                hgrow = Priority.SOMETIMES
            }
        }
        // Center
        textfield {
            prefWidth = 600.0
            prefHeight = 30.0
        }
        // Space
        pane() {
            hboxConstraints {
                hgrow = Priority.SOMETIMES
            }
        }
        // Right side
        button {
            prefWidth = 30.0
            prefHeight = prefWidth
        }
        button {
            prefWidth = 30.0
            prefHeight = prefWidth
        }
    }
}

class CenterView: View() {
    override val root = InlineCssTextArea()
    init {
        root.padding = Insets(20.0, 20.0, 20.0, 20.0)
    }
}
