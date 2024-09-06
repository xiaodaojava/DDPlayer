package com.example.ttplayer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.text.DecimalFormat;

public class VideoPlayerController {

    public MediaView mediaView;

    public VBox vbox;

    public AnchorPane anchorPane;


    public ProgressBar progressBar;


    public ButtonBar buttonBar;
    public Label currentTime;

    private File mediaFile;

    private MediaPlayer mediaPlayer;


    public void start(File file) {
        this.mediaFile = file;
        Media media = new Media(mediaFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        // 更新AnchorPane大小时，重新计算VBox的位置和大小
        anchorPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double left = 10.0;
            double right = 10.0;
            double width = newValue.doubleValue() - left - right;
            AnchorPane.setLeftAnchor(vbox, left);
            AnchorPane.setRightAnchor(vbox, right);
            vbox.setPrefWidth(width);

        });

        anchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            double top = 10.0;
            double bottom = 10.0;
            double height = newValue.doubleValue() - top - bottom;
            AnchorPane.setTopAnchor(vbox, top);
            AnchorPane.setBottomAnchor(vbox, bottom);
            vbox.setPrefHeight(height-40);
        });

        //如果 VBox 的 fillWidth 属性设置为 true，则 VBox 的子节点将尝试扩展其宽度以填充 BorderPane 的宽度。
        //如果 fillWidth 属性设置为 false，则子节点将不会自动扩展其宽度，而是保持其固有的宽度。
        mediaView.fitWidthProperty().bind(vbox.widthProperty());
        mediaView.fitHeightProperty().bind(vbox.heightProperty());

        // 将ProgressBar的宽度绑定到VBox的宽度
        progressBar.prefWidthProperty().bind(vbox.widthProperty());

        // 设置ProgressBar的进度
        mediaPlayer.setOnReady(() -> {
            


            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                double totalSeconds = mediaPlayer.getTotalDuration().toSeconds();
                double progress = newValue.toSeconds() / totalSeconds;
                progressBar.setProgress(progress);

                DecimalFormat df = new DecimalFormat("00");
                String currentTimeStr = df.format(newValue.toSeconds() / 3600)
                        + ":" + df.format((newValue.toSeconds() % 3600) / 60)
                        + ":" + df.format(newValue.toSeconds() % 60);

                String totalTimeStr = df.format(totalSeconds / 3600)
                        + ":" + df.format((totalSeconds % 3600) / 60)
                        + ":" + df.format(totalSeconds % 60);

                currentTime.setText(currentTimeStr+"/"+totalTimeStr);
            });
        });

        progressBar.setOnMouseClicked(event -> {
            // 获取点击位置的屏幕坐标
            Point2D point = new Point2D(event.getSceneX(), event.getSceneY());
            // 将屏幕坐标转换为进度条上的位置（假设进度条宽度为300）
            double progress = point.getX() / progressBar.getWidth();
            // 计算播放时间
            Duration newTime = mediaPlayer.getTotalDuration().multiply(progress);
            // 设置 MediaPlayer 的播放时间
            mediaPlayer.seek(newTime);
        });



        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
        mediaPlayer.setMute(true);



    }
}
