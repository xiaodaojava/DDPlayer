package com.example.ttplayer;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HelloApplication extends Application {


    // 用于存储当前按下的按键
    private Set<Integer> pressedKeys = new HashSet<>();


    // 用于保存剪贴板历史的列表
    private List<String> clipboardHistory = new ArrayList<>();


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/ttplayer/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),600,400);
        stage.setScene(scene);
        stage.setTitle("DDTool-支持多个视频同时播放，支持截图，支持粘贴板备忘");
        stage.show();

        // 注册全局键盘事件，这里要注意，注册之后，就不支持通过stage来关程序了
        registerGlobalHotkey();

        // 创建一个定时器，每隔1秒检查一次剪贴板内容
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                String currentContent = clipboard.getString();

                // 取list中最后一个元素
                String lastClipboardContent = clipboardHistory.isEmpty() ? "" : clipboardHistory.get(clipboardHistory.size() - 1);
                // 如果剪贴板内容变化，则记录下来
                if (!currentContent.equals(lastClipboardContent)) {
                    clipboardHistory.add(currentContent);
                }
                // 如果list中超过了10个元素，则删除第一个元素
                if (clipboardHistory.size() > 10) {
                    clipboardHistory.remove(0);
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


        // 系统托盘相关的
        stage.setOnCloseRequest(e -> {
            SunflowerTray.getInstance().hide(stage);
            SunflowerTray.getInstance().listen(stage);
        });

    }


    private void registerGlobalHotkey() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                System.out.println("Key Pressed: " + e.getKeyCode() + " - " + NativeKeyEvent.getKeyText(e.getKeyCode()));

                // 记录按下的按键
                pressedKeys.add(e.getKeyCode());
                // 检查是否按下组合键 Ctrl + Shift + H
                if (pressedKeys.contains(NativeKeyEvent.VC_CONTROL) &&
                        pressedKeys.contains(NativeKeyEvent.VC_SHIFT) &&
                        pressedKeys.contains(NativeKeyEvent.VC_H)) {
                    System.out.println("Ctrl + Shift + H 按下!");

                    // 输出剪贴板的内容
                    System.out.println("剪贴板内容：" + clipboardHistory.toString());

                    Platform.runLater(()->{
                        showClipboardHistoryMenu();
                    });
                }

            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent e) {
                // Not used
                // 按键释放时从集合中移除
                pressedKeys.remove(e.getKeyCode());
                System.out.println("Key Released: " + e.getKeyCode() + " - " + NativeKeyEvent.getKeyText(e.getKeyCode()));

            }

            @Override
            public void nativeKeyTyped(NativeKeyEvent e) {
                // Not used
            }
        });
    }

    private void showClipboardHistoryMenu() {

        // 创建列表以显示剪贴板内容
        ListView<String> listView = new ListView<>();
        // clipboardHistory 倒序循环添加到listView中
        for (int i = clipboardHistory.size() - 1; i >= 0; i--) {
            listView.getItems().add(clipboardHistory.get(i));
        }

        // 添加双击事件
        listView.setOnMouseClicked(event -> {
            // 关闭窗口
            ((Stage) listView.getScene().getWindow()).close();

            String selectedContent = listView.getSelectionModel().getSelectedItem();
            if (selectedContent != null) {
                pasteToClipboard(selectedContent);
            }

        });


        // 创建新的 Stage 来显示 ListView
        Stage stage = new Stage();
        StackPane pane = new StackPane(listView);
        Scene scene = new Scene(pane);
        stage.setScene(scene);

        // 设置窗口在鼠标位置显示
        try {
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
            stage.setX(mouseLocation.getX());
            stage.setY(mouseLocation.getY());
        } catch (Exception e) {
            e.printStackTrace();
        }

        stage.show();
    }

    private void pasteToClipboard(String content) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);
    }


    public static void main(String[] args) {
        launch();
    }

}