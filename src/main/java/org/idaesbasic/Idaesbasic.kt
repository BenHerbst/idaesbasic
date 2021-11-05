package org.idaesbasic

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage


class Idaesbasic : Application() {
    override fun start(stage: Stage) {
        try {
            // Show welcome screen
            val root = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/WelcomeScreen.fxml"))
            val scene = Scene(root)
            scene.stylesheets.add(javaClass.getResource("/styles/dialog.css").toExternalForm())
            stage.scene = scene
            stage.title = "Idaesbasic / Welcome - 0.9.0 - Beta"
            stage.height = 530.0
            stage.width = 650.0
            stage.isMaximized = false
            stage.isResizable = false
            // Close all timertasks on main window close
            stage.onCloseRequest = EventHandler {
                Platform.exit()
                System.exit(0)
            }
            // Add icon
            stage.icons.add(Image(Main::class.java.getResourceAsStream("/icon.png")))
            stage.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Idaesbasic::class.java)
        }
    }
}