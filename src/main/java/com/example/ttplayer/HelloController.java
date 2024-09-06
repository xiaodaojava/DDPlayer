package com.example.ttplayer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

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

                // 新窗口
                Stage videoStage = new Stage();
                videoStage.setTitle(file.getName());

                //Modality.NONE: 不阻止任何其他窗口的操作。这是默认的行为。
                //Modality.APPLICATION_MODAL: 阻止应用程序中的所有其他窗口的操作，除了操作系统级别的窗口（如任务管理器）。
                //Modality.WINDOW_MODAL: 阻止特定父窗口的操作。这意味着只有指定的父窗口不能接收输入，其他窗口仍然可以操作。
                videoStage.initModality(Modality.NONE);
                videoStage.setMinWidth(600);
                videoStage.setMinHeight(500);
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/com/example/ttplayer/video-player.fxml"));
                Parent root = null;
                try {
                    root = fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                VideoPlayerController videoPlayerController = fxmlLoader.getController();
                videoPlayerController.start(file);
                Scene scene = new Scene(root,600,500);
                videoStage.setScene(scene);
                videoStage.showAndWait();




//                VBox root = new VBox();
//
//                // 创建进度条
//                ProgressBar progressBar = new ProgressBar();
//                progressBar.setPrefWidth(Double.MAX_VALUE);
//                progressBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent event) {
//                        // 获取点击位置的屏幕坐标
//                        Point2D point = new Point2D(event.getSceneX(), event.getSceneY());
//                        // 将屏幕坐标转换为进度条上的位置（假设进度条宽度为300）
//                        double progress = point.getX() / progressBar.getWidth();
//                        // 计算播放时间
//                        Duration newTime = mediaPlayer.getTotalDuration().multiply(progress);
//                        // 设置 MediaPlayer 的播放时间
//                        mediaPlayer.seek(newTime);
//                    }
//                });
//
//                // 将进度条绑定到 MediaPlayer 的播放进度
//                DoubleBinding progressBinding = Bindings.createDoubleBinding(() -> {
//                    Duration currentTime = mediaPlayer.getCurrentTime();
//                    Duration totalDuration = mediaPlayer.getTotalDuration();
//                    if (totalDuration == null) {
//                        return 0.0;
//                    }
//                    return currentTime.toSeconds() / totalDuration.toSeconds();
//                }, mediaPlayer.currentTimeProperty(), mediaPlayer.totalDurationProperty());
//
//
//                progressBar.progressProperty().bind(progressBinding);
//
//
//                // 绑定 MediaView 的大小到 StackPane
//                mediaView.fitWidthProperty().bind(root.widthProperty());
//                mediaView.fitHeightProperty().bind(root.heightProperty());
//
//                root.getChildren().addAll(mediaView,progressBar);
//                VBox.setVgrow(mediaView, javafx.scene.layout.Priority.ALWAYS);
//                root.setFillWidth(true);
//
//                // 大的布局容器
//                Scene fileScene = new Scene(root,  600,400);
//                videoStage.setScene(fileScene);
//                videoStage.show();
//
//                mediaPlayer.play();
            }
        });
    }
}