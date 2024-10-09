module red.lixiang.dd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.swing;
    requires com.github.kwhat.jnativehook;


    opens red.lixiang.dd to javafx.fxml;
    exports red.lixiang.dd;
}