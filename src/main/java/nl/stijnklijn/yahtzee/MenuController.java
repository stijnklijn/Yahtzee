package nl.stijnklijn.yahtzee;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class MenuController {

    private static final String IMAGE = "file:src/main/resources/nl/stijnklijn/yahtzee/dice.png";

    @FXML ImageView imageView;

    public void startGame() throws IOException {
        YahtzeeLauncher.getSceneManager().showNamesScene();
    }

    public void exit() {
        System.exit(0);
    }

    public void setup() {
        imageView.setImage(new Image(IMAGE));
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
    }
}
