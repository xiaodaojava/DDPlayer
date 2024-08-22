package com.example.ttplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),400,300);
        stage.setScene(scene);
        stage.setTitle("DDPlayer-支持多个视频同时播放");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}