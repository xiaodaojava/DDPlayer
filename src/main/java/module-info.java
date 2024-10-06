module com.example.ttplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.swing;
    requires com.github.kwhat.jnativehook;


    opens com.example.ttplayer to javafx.fxml;
    exports com.example.ttplayer;
}