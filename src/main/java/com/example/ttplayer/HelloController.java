package com.example.ttplayer;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class HelloController {
    @FXML
    private Button openFileButton;
    @FXML
    private HBox mediaContainer;

    public void initialize() {
        openFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                Media media = new Media(file.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);

                // 新窗口
                Stage secondaryStage = new Stage();
                secondaryStage.setTitle(file.getName());
                VBox root = new VBox();

                // 创建进度条
                ProgressBar progressBar = new ProgressBar();
                progressBar.setPrefWidth(Double.MAX_VALUE);
                progressBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // 获取点击位置的屏幕坐标
                        Point2D point = new Point2D(event.getSceneX(), event.getSceneY());
                        // 将屏幕坐标转换为进度条上的位置（假设进度条宽度为300）
                        double progress = point.getX() / progressBar.getWidth();
                        // 计算播放时间
                        Duration newTime = mediaPlayer.getTotalDuration().multiply(progress);
                        // 设置 MediaPlayer 的播放时间
                        mediaPlayer.seek(newTime);
                    }
                });

                // 将进度条绑定到 MediaPlayer 的播放进度
                DoubleBinding progressBinding = Bindings.createDoubleBinding(() -> {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    Duration totalDuration = mediaPlayer.getTotalDuration();
                    if (totalDuration == null) {
                        return 0.0;
                    }
                    return currentTime.toSeconds() / totalDuration.toSeconds();
                }, mediaPlayer.currentTimeProperty(), mediaPlayer.totalDurationProperty());


                progressBar.progressProperty().bind(progressBinding);


                // 绑定 MediaView 的大小到 StackPane
                mediaView.fitWidthProperty().bind(root.widthProperty());
                mediaView.fitHeightProperty().bind(root.heightProperty());

                root.getChildren().addAll(mediaView,progressBar);
                VBox.setVgrow(mediaView, javafx.scene.layout.Priority.ALWAYS);
                root.setFillWidth(true);

                // 大的布局容器
                Scene fileScene = new Scene(root,  600,400);
                secondaryStage.setScene(fileScene);
                secondaryStage.show();

                mediaPlayer.play();
            }
        });
    }
}