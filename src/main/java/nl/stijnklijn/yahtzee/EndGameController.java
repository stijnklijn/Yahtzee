package nl.stijnklijn.yahtzee;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class EndGameController {

    private static final String PATH = "file:src/main/resources/nl/stijnklijn/yahtzee/";
    private static final String message = "You will lose the current game data. Proceed?";

    @FXML Text title;
    @FXML ImageView imageView;

    String player1Name;
    String player2Name;

    Stage secondaryStage;

    public void playAgainSamePlayers() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.OK)) {
            secondaryStage.close();
            YahtzeeLauncher.getSceneManager().showGameScene(player1Name, player2Name);
        }
    }

    public void playAgainDifferentPlayers() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.OK)) {
            secondaryStage.close();
            YahtzeeLauncher.getSceneManager().showNamesScene();
        }
    }

    public void backToMainMenu() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.OK)) {
            secondaryStage.close();
            YahtzeeLauncher.getSceneManager().showMenuScene();
        }
    }

    public void backToGame() {
        secondaryStage.close();
    }

    public void setup(Stage secondaryStage, String player1Name, String player2Name, String message) {
        this.secondaryStage = secondaryStage;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        title.setText(message);

        Random random = new Random();
        int imageNumber = random.nextInt(10);
        imageView.setImage(new Image(PATH + imageNumber + ".gif"));
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
    }
}
