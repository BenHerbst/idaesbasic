package org.idaesbasic

import tornadofx.*

class MainStyle: Stylesheet() {

   companion object {
       val styledTextArea by cssclass()
       val caret by cssclass()
       val squeezebox by cssclass()
       val form by cssclass()
       val lineno by cssclass()

       private val draculaBackgroundColor = c("#282a36")
       private val draculaForegroundColor = c("#44475a")
       private val draculaSelectColor = c("#6272a4")
       private val draculaTextColor = c("#f8f8f2")
   }

    init {
        button {
            backgroundColor = multi(draculaForegroundColor)
            textFill = draculaTextColor
            and (hover) {
                backgroundColor = multi(draculaSelectColor)
            }
        }
        toolBar {
            backgroundColor = multi(draculaBackgroundColor)
        }
        textField {
            backgroundColor = multi(draculaForegroundColor)
            textFill = draculaTextColor
        }
        styledTextArea {
            backgroundColor = multi(draculaBackgroundColor)
            textFill = draculaTextColor
            fontSize = 14.px
            text {
                fill = draculaTextColor
            }
        }
        caret {
            stroke = draculaTextColor
        }
        squeezebox {
            backgroundColor = multi(draculaBackgroundColor)
        }
        titledPane {
            textFill = draculaTextColor
            title {
                backgroundColor = multi(draculaBackgroundColor)
            }
            content {
                borderColor = multi(box(draculaBackgroundColor))
                backgroundColor = multi(draculaBackgroundColor)
            }
        }
        root {
            backgroundColor = multi(draculaBackgroundColor)
        }
        form {
            backgroundColor = multi(draculaBackgroundColor)
            textFill = draculaTextColor
        }
        label {
            textFill = draculaTextColor
        }
        lineno {
            backgroundColor = multi(draculaBackgroundColor)
        }
    }
}