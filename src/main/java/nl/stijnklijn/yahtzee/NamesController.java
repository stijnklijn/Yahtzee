package nl.stijnklijn.yahtzee;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class NamesController {

    @FXML TextField player1NameField;
    @FXML TextField player2NameField;

    public void startGame() throws IOException {
        String player1Name = player1NameField.getText();
        String player2Name = player2NameField.getText();
        if (player1Name.length() > 0 && player1Name.length() <= 15 && player2Name.length() > 0 && player2Name.length() <= 15 && !player1Name.equals(player2Name)) {
            YahtzeeLauncher.getSceneManager().showGameScene(player1Name, player2Name);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter two different player names between 1 and 15 characters");
            alert.show();
        }
    }

    public void back() throws IOException {
        YahtzeeLauncher.getSceneManager().showMenuScene();
    }
}
