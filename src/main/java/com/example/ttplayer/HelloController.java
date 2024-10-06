package com.example.ttplayer;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
            Canvas screenCanvas = new Canvas(fxImage.getWidth(), fxImage.getHeight());

            GraphicsContext gc = screenCanvas.getGraphicsContext2D();
            gc.drawImage(fxImage, 0, 0);

            // 将Canvas放入ScrollPane中
            ScrollPane scrollPane = new ScrollPane(screenCanvas);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            VBox vbox = new VBox(scrollPane);
            Scene scene = new Scene(vbox);
            Stage primaryStage = new Stage();
            // 让 stage是无边框的
            primaryStage.initStyle(StageStyle.UNDECORATED);
            // 注册键盘事件监听器
            scene.setOnKeyPressed(event1 -> {
                // 检查是否按下了ESC键
                if (event1.getCode() == KeyCode.ESCAPE) {
                    primaryStage.close(); // 关闭当前的Stage
                }
            });

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
//            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            // 获取所有屏幕设备
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] devices = ge.getScreenDevices();
            GraphicsDevice currentDevice = devices[0];
            // 获取屏幕大小
            java.awt.Rectangle rect = new java.awt.Rectangle(currentDevice.getDefaultConfiguration().getBounds());
            // 创建Robot实例
            Robot robot = new Robot();
            // 捕获屏幕
            java.awt.Image nativeResImage;
            MultiResolutionImage mrImage = robot.createMultiResolutionScreenCapture(rect);
            List<java.awt.Image> resolutionVariants = mrImage.getResolutionVariants();
            if (resolutionVariants.size() > 1) {
                nativeResImage = resolutionVariants.get(1);
            } else {
                nativeResImage = resolutionVariants.get(0);
            }
            return (BufferedImage) nativeResImage;
//            return robot.createScreenCapture(rect);
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