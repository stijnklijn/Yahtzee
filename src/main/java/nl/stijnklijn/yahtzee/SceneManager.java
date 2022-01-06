package nl.stijnklijn.yahtzee;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showMenuScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(YahtzeeLauncher.class.getResource("menu-view.fxml"));
        Pane pane = fxmlLoader.load();
        MenuController controller = fxmlLoader.getController();
        controller.setup();
        pane.getStylesheets().add(MenuController.class.getResource("menu-view.css").toExternalForm());
        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setTitle("Yahtzee");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void showNamesScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(YahtzeeLauncher.class.getResource("names-view.fxml"));
        Pane pane = fxmlLoader.load();
        pane.getStylesheets().add(NamesController.class.getResource("names-view.css").toExternalForm());
        Scene scene = new Scene(pane, 300, 250);
        primaryStage.setTitle("Enter player names");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void showGameScene(String player1Name, String player2Name) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(YahtzeeLauncher.class.getResource("game-view.fxml"));
        Pane pane = fxmlLoader.load();
        GameController controller = fxmlLoader.getController();
        controller.setup(player1Name, player2Name);
        pane.getStylesheets().add(GameController.class.getResource("game-view.css").toExternalForm());
        Scene scene = new Scene(pane, 850, 650);
        primaryStage.setTitle("New Game");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void showEndGameScene(String player1Name, String player2Name, String message) throws IOException {
        Stage secondaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(YahtzeeLauncher.class.getResource("endgame-view.fxml"));
        Pane pane = fxmlLoader.load();
        EndGameController controller = fxmlLoader.getController();
        controller.setup(secondaryStage, player1Name, player2Name, message);
        pane.getStylesheets().add(EndGameController.class.getResource("endgame-view.css").toExternalForm());
        Scene scene = new Scene(pane, 500, 550);
        secondaryStage.setTitle(message);
        secondaryStage.setScene(scene);
        secondaryStage.centerOnScreen();
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.setResizable(false);
        secondaryStage.show();
    }
}
