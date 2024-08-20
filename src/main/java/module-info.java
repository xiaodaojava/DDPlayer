module com.example.ttplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.example.ttplayer to javafx.fxml;
    exports com.example.ttplayer;
}