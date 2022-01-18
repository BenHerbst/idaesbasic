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

    exports org.idaesbasic;
}