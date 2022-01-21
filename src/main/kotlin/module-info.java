module idaesbasic.org.idaesbasic {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires tornadofx;
    requires org.fxmisc.richtext;
    requires kotlin.stdlib;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires tornadofx.controlsfx;

    opens org.idaesbasic to tornadofx;
    opens org.idaesbasic.buffer to tornadofx;
    opens org.idaesbasic.buffer.run to tornadofx;
    opens org.idaesbasic.sidepanel to tornadofx;
    opens org.idaesbasic.powerline to tornadofx;

    exports org.idaesbasic;
}