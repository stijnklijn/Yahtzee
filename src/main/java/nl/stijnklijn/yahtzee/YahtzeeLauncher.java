package nl.stijnklijn.yahtzee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class YahtzeeLauncher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(YahtzeeLauncher.class.getResource("game-view.fxml"));
        Pane pane = fxmlLoader.load();
        GameController controller = fxmlLoader.getController();
        controller.setup();
        pane.getStylesheets().add(GameController.class.getResource("game-view.css").toExternalForm());
        Scene scene = new Scene(pane, 800, 600);
        stage.setTitle("Yahtzee");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}