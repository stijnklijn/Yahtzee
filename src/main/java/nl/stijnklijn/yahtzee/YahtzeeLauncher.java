package nl.stijnklijn.yahtzee;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class YahtzeeLauncher extends Application {

    private static Stage primaryStage;

    private static SceneManager sceneManager = null;

    public static SceneManager getSceneManager() {
        if (sceneManager == null) {
            sceneManager = new SceneManager(primaryStage);
        }
        return sceneManager;
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        getSceneManager().showMenuScene();
    }

    public static void main(String[] args) {
        launch();
    }
}