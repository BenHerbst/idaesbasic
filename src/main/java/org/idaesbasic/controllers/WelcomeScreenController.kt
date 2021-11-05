package org.idaesbasic.controllers

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.idaesbasic.models.Projects
import java.io.IOException

class WelcomeScreenController {
    var registeredProjects = Projects()

    @FXML
    private lateinit var projectListBox: ChoiceBox<Any>

    @Throws(IOException::class)
    fun addProject(event: ActionEvent) {
        // Load main scene
        val loader = FXMLLoader()
        loader.location = javaClass.getResource("/fxml/MainView.fxml")
        val parent = loader.load<Parent>()
        val node = event.source as Node
        val thisStage = node.scene.window as Stage
        val scene = Scene(parent)
        // Change stage
        thisStage.hide()
        thisStage.isMaximized = true
        thisStage.isResizable = true
        thisStage.title = "Idaesbasic / Main - 0.9.0 - Beta"
        thisStage.scene = scene
        thisStage.show()
        // Show open project window
        val controller = loader.getController<MainController>()
        val chooser = DirectoryChooser()
        controller.addNewProject(chooser.showDialog(null))
    }

    @FXML
    fun createNewProject(event: ActionEvent?) {
    }

    @FXML
    @Throws(IOException::class)
    fun initialize() {
        registeredProjects.loadProjectListFromUserFiles()

        println(registeredProjects.projectList)
        projectListBox.items = FXCollections.observableArrayList(registeredProjects.projectList)
        println(projectListBox.items)
        projectListBox.selectionModel?.selectedItemProperty()?.addListener(object : ChangeListener<Any> {
            override fun changed(ov: ObservableValue<*>, value: Any?, newValue: Any?) {
                val loader = FXMLLoader()
                loader.location = javaClass.getResource("/fxml/MainView.fxml")
                var parent: Parent? = null
                try {
                    parent = loader.load()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                val node = projectListBox as Node?
                val thisStage = node!!.scene.window as Stage
                val scene = Scene(parent)
                // Change stage
                thisStage.hide()
                thisStage.isMaximized = true
                thisStage.isResizable = true
                thisStage.title = "Idaesbasic / Main - 0.9.0 - Beta"
                thisStage.scene = scene
                val controller = loader.getController<MainController>()
                controller.openRegisteredProject(newValue.toString())
                thisStage.show()
            }
        })
    }
}