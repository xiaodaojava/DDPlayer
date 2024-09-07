package com.example.ttplayer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
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

    /**
     * 时间标签
     */
    public Label currentTime;

    /**
     * 静音
     */
    public Button muteBtn;

    /**
     * 音量加
     */
    public Button volumeAddBtn;

    /**
     * 音量减
     */
    public Button volumeSubBtn;

    /**
     * 播放/暂停
     */
    public Button playBtn;

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
            vbox.setPrefHeight(height - 40);
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

                currentTime.setText(currentTimeStr + "/" + totalTimeStr);
            });
        });


        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
        mediaPlayer.setMute(true);


    }

    public void playBtnClick(MouseEvent mouseEvent) {
        // 如果mediaPlay正在播放，则暂停。 如果是暂停则播放
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            tooltipNotice(mouseEvent,"暂停");
        } else {
            mediaPlayer.play();
            tooltipNotice(mouseEvent,"播放");
        }
    }

    public void volumeSubBtnClick(MouseEvent mouseEvent) {
        // 判断音量有没有到最低，如果没有的话，则减0.1
        if (mediaPlayer.getVolume() <= 0) {
            tooltipNotice(mouseEvent,"当前音量已最低");
            return;
        }
        mediaPlayer.setVolume(mediaPlayer.getVolume() - 0.1);
        tooltipNotice(mouseEvent,"当前音量:"+mediaPlayer.getVolume()*10);


    }

    public void volumeAddBtnClick(MouseEvent mouseEvent) {
        if (mediaPlayer.getVolume() >= 1) {
            return;
        }
        // 如果是静音，先解除静音
        if (mediaPlayer.isMute()) {
            mediaPlayer.setMute(false);
            // 然后设置为最小音量
            mediaPlayer.setVolume(0.1);
        }
        // 调大音量
        mediaPlayer.setVolume(mediaPlayer.getVolume() + 0.1);
        tooltipNotice(mouseEvent,"当前音量:"+mediaPlayer.getVolume()*10);

    }

    /**
     * 解除/设置静音
     *
     * @param mouseEvent
     */
    public void muteBtnClick(MouseEvent mouseEvent) {
        mediaPlayer.setMute(!mediaPlayer.isMute());
        tooltipNotice(mouseEvent,"当前静音状态:"+mediaPlayer.isMute());

    }

    public void progressBarClick(MouseEvent mouseEvent) {
        // 获取点击位置的屏幕坐标
        Point2D point = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        // 将屏幕坐标转换为进度条上的位置（假设进度条宽度为300）
        double progress = point.getX() / progressBar.getWidth();
        // 计算播放时间
        Duration newTime = mediaPlayer.getTotalDuration().multiply(progress);
        // 设置 MediaPlayer 的播放时间
        mediaPlayer.seek(newTime);
    }

    /**
     * 根据事件，显示相应的Tooltip， 2秒后隐藏
     */
    public void tooltipNotice (Event event, String text) {
        Node sourceNode = (Node) event.getSource();
        Point2D point2D = sourceNode.localToScene(0, 0);
        double buttonSceneX = point2D.getX();
        double buttonSceneY = point2D.getY();

        // 获取场景在窗口中的位置
        double scenePosInWindow = sourceNode.getScene().getWindow().getX() + buttonSceneX;
        double scenePosInWindowY = sourceNode.getScene().getWindow().getY() + buttonSceneY;


        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(sourceNode, tooltip);
        // 设置Popup的位置
        tooltip.setX(scenePosInWindow + 5); // 按钮右边5像素处
        tooltip.setY(scenePosInWindowY - tooltip.getHeight());
        tooltip.show(sourceNode.getScene().getWindow());
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), _ -> tooltip.hide())
        );
        timeline.play();
    }
}
