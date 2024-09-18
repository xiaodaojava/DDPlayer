package com.example.ttplayer;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;


import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

public class HelloController {

    /**
     * 截图的button
     */
    public Button captureButton;


    @FXML
    private Button openFileButton;

    @FXML
    private HBox mediaContainer;

    private Pane canvas;
    private Rectangle dragRectangle = null;
    private Point2D startPoint = null;

    public void initialize() {
        openFileButton.setOnAction(_ -> {
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
            }
        });



        canvas = new Pane();
        canvas.setStyle("-fx-background-color: rgba(0,0,0,0.5);"); // 灰色遮罩

        Canvas screenCanvas = new Canvas(Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());

        captureButton.setOnAction(event -> {


            // 显示灰色遮罩并开始监听鼠标事件
//            canvas.getChildren().clear();
//            canvas.setPrefSize(Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
//            canvas.setVisible(true);

            // 绘制屏幕到canvas上
            BufferedImage screenImage = captureScreen();
            // 转换为JavaFX的Image
            Image fxImage = SwingFXUtils.toFXImage(screenImage, null);
            // 绘制到Canvas
            GraphicsContext gc = screenCanvas.getGraphicsContext2D();
            gc.drawImage(fxImage, 0, 0);
            VBox vbox = new VBox(screenCanvas);
            StackPane stackPane = new StackPane(vbox);

            Scene scene = new Scene(stackPane);
            Stage primaryStage = new Stage();
            // 让 stage是无边框的
//            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(scene);
            primaryStage.show();

//            canvas.setOnMousePressed(me -> {
//                startPoint = new Point2D(me.getScreenX(), me.getScreenY());
//            });

//            canvas.setOnMouseDragged(me -> {
//                if (startPoint != null) {
//                    double x = Math.min(me.getScreenX(), startPoint.getX());
//                    double y = Math.min(me.getScreenY(), startPoint.getY());
//                    double width = Math.abs(me.getScreenX() - startPoint.getX());
//                    double height = Math.abs(me.getScreenY() - startPoint.getY());
//
//                    if (dragRectangle == null) {
//                        dragRectangle = new Rectangle(x, y, width, height);
//                        dragRectangle.setFill(Color.TRANSPARENT);
//                        dragRectangle.setStroke(Color.WHITE);
//                        canvas.getChildren().add(dragRectangle);
//                    } else {
//                        dragRectangle.setX(x);
//                        dragRectangle.setY(y);
//                        dragRectangle.setWidth(width);
//                        dragRectangle.setHeight(height);
//                    }
//                }
//            });

//            canvas.setOnMouseReleased(me -> {
//                if (dragRectangle != null) {
//                    Bounds bounds = dragRectangle.getBoundsInLocal();
//                    WritableImage screenshot = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
//                    Robot robot;
//                    try {
//                        robot = new Robot();
//                        Image image = SwingFXUtils.toFXImage(
//                                robot.createScreenCapture(new java.awt.Rectangle((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getWidth(), (int) bounds.getHeight())),
//                                null);
//                        screenshot = new WritableImage(image.getPixelReader(), (int) bounds.getWidth(), (int) bounds.getHeight());
//
//                        // 清除遮罩和拖拽矩形
//                        canvas.getChildren().clear();
//                        canvas.setVisible(false);
//
//                        // 显示截图
//                        ImageView imageView = new ImageView(screenshot);
//                        canvas.getChildren().add(imageView);
//
//                        // 保存截图
//                        saveScreenshot(screenshot);
//                    } catch (  AWTException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        });

//        BorderPane root = new BorderPane();
//        root.setCenter(canvas);
//
//        Scene scene = new Scene(root, 400, 400);
//        Stage primaryStage = new Stage();
//        primaryStage.setScene(scene);
//        primaryStage.show();

    }

    private BufferedImage captureScreen() {
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            // 获取屏幕大小
            java.awt.Rectangle rect = new java.awt.Rectangle(screenSize);
            // 创建Robot实例
            Robot robot = new Robot();
            // 捕获屏幕
            return robot.createScreenCapture(rect);
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void saveScreenshot(WritableImage screenshot) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Screenshot");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("JPEG files (*.jpg)", "*.jpg"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*")
        );
        fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}