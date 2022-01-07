package nl.stijnklijn.yahtzee;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameController {

    private static final String PATH = "file:src/main/resources/nl/stijnklijn/yahtzee/";
    private static final int NUMBER_OF_DICE = 5;
    private static final int DICE_MAX = 6;
    private static final int NUMBER_OF_FIELDS = 14;
    private static final int MAX_TURNS = 3;

    @FXML private HBox player1Pane;
    @FXML private HBox player2Pane;
    @FXML private GridPane fieldPane;
    @FXML private HBox dicePane;
    @FXML private Button btRoll;
    @FXML private Button btPlay;

    int[][] score = new int[2][NUMBER_OF_FIELDS];                  //Holding the scores for each player and each field
    StackPane[][] field = new StackPane[2][NUMBER_OF_FIELDS];       //Holding stackpanes for presenting each score
    Boolean[][] isRegistered = new Boolean[2][NUMBER_OF_FIELDS];    //Representing whether a score has been registered for each field

    int currentPlayer = 0;  //Which player's turn is it
    int currentTurn = 0;    //Which turn is it
    int currentIndex;       //Index of the currently selected field
    int currentScore;       //Lastly calculated score

    String player1Name;
    String player2Name;
    Text player1Score;
    Text player2Score;

    Group turnTrackers;     //Group of three circles on the roll button tracking and showing which turn it is

    int[] dice = new int[NUMBER_OF_DICE];                           //Integers representing the dice roll
    boolean[] saveDice = new boolean[NUMBER_OF_DICE];               //Representing whether dice should be saved for the next roll
    double rDice = 0.0;                                             //Current rotation for dice animation
    double drDice = 1.0;                                            //Rotation change for dice animation

    //Inline styles
    String inactivePlayer = "-fx-fill: grey; -fx-font-size: 30; -fx-font-weight: normal";
    String activePlayer = "-fx-fill: white; -fx-font-size: 30;-fx-font-weight: bold";
    String fieldStyle = "-fx-fill: white; -fx-font-weight: bold";

    public void roll() {
        clearLastField();

        //Clear previous dice, then roll each die that hasn't been saved, and display the new dice
        dicePane.getChildren().clear();
        Random random = new Random();
        for (int i = 0; i < NUMBER_OF_DICE; i++) {
            if (!saveDice[i]) {
                int roll = random.nextInt(DICE_MAX);
                dice[i] = roll + 1;
            }
            Die die = new Die(dice[i], saveDice[i]);
            die.setId("d" + i);
            die.setOnMouseClicked(this::saveDice);
            dicePane.getChildren().add(die);
        }

        //Show dice animation
        Timeline animation = new Timeline(new KeyFrame(Duration.millis(5), e -> moveDie()));
        animation.setCycleCount(80);
        animation.play();

        //Increase turn by one
        Circle c = (Circle) turnTrackers.getChildren().get(currentTurn);
        c.setFill(Color.WHITE);
        currentTurn++;

        //Disable roll button when max turns is reached
        if (currentTurn == MAX_TURNS) {
            btRoll.setDisable(true);
        }
    }

    private void moveDie() {
        //Invert the rotation direction at -10 and 10 degrees of rotation
        if (rDice == -10 || rDice == 10) {
            drDice *= -1.0;
        }

        //Change rotation accordingly for all dice
        rDice += drDice;
        for (int i = 0; i < NUMBER_OF_DICE; i++) {
            if (!saveDice[i]) {
                dicePane.getChildren().get(i).setRotate(rDice);
            }
        }
    }

    public void select(MouseEvent e) {
        //Find out to which player the field that has been clicked belongs by extracting the player number from the id
        Rectangle clicked = (Rectangle) (e.getSource());
        int playerClicked = Integer.parseInt(clicked.getParent().getId().substring(1, 2));

        //Only select the field if the player that the field belongs to is the current player
        if (playerClicked == currentPlayer) {

            //Find out which field has been clicked by extracting the field number from the id
            currentIndex = Integer.parseInt(clicked.getParent().getId().substring(2));

            //Find out how many points the field is worth
            switch (currentIndex) {
                case 7 -> currentScore = calculateThreeOfAKind();
                case 8 -> currentScore = calculateFourOfAKind();
                case 9 -> currentScore = calculateFullHouse();
                case 10 -> currentScore = calculateSmallStraight();
                case 11 -> currentScore = calculateLargeStraight();
                case 12 -> currentScore = calculateYahtZee();
                case 13 -> currentScore = calculateChance();
                default -> currentScore = calculateSum(currentIndex + 1);
            }

            //Display the points if the dice have been rolled and the field is not registered yet
            //Then enable the play button
            if (dice[0] != 0 && !isRegistered[currentPlayer][currentIndex]) {
                clearLastField();
                changeField(field[currentPlayer][currentIndex], Color.BLUE, currentScore + "");
                btPlay.setDisable(false);
            }
        }
    }

    private int calculateSum(int index) {
        //Calculate the sum of the dice that rolled the given (index) number
        return IntStream.of(dice).filter(n -> n == index).sum();
    }

    private int calculateThreeOfAKind() {
        //For each number between 1 and the maximum, check whether at least three dice rolled that number
        //If so, return the sum of all dice
        for (int i = 1; i <= DICE_MAX; i++) {
            int number = i;
            if (IntStream.of(dice).filter(n -> n == number).count() >= 3) {
                return IntStream.of(dice).sum();
            }
        }
        return 0;
    }

    private int calculateFourOfAKind() {
        //For each number between 1 and the maximum, check whether at least four dice rolled that number
        //If so, return the sum of all dice
        for (int i = 1; i <= DICE_MAX; i++) {
            int number = i;
            if (IntStream.of(dice).filter(n -> n == number).count() >= 4) {
                return IntStream.of(dice).sum();
            }
        }
        return 0;
    }

    private int calculateFullHouse() {
        //Sort the dice, then check whether the first two and last three are the same
        //or the first three and last two are the same.
        int[] sortedDice = IntStream.of(dice).sorted().toArray();
        if (sortedDice[1] == sortedDice[0] && sortedDice[3] == sortedDice[2] && sortedDice[4] == sortedDice[3] ||
            sortedDice[1] == sortedDice[0] && sortedDice[2] == sortedDice[1] && sortedDice[4] == sortedDice[3]) {
            return 25;
        } else {
            return 0;
        }
    }

    private int calculateSmallStraight() {
        //Sort the dice and keep only distinct dice.
        //Then, if the remaining array has length 4, check whether all dice are consecutive
        //If the remaining array has length 5, check whether the first four or the last four are consecutive.
        int[] sortedDistinctDice = IntStream.of(dice).sorted().distinct().toArray();
        if (sortedDistinctDice.length == 4 && sortedDistinctDice[1] - sortedDistinctDice[0] == 1 && sortedDistinctDice[2] - sortedDistinctDice[1] == 1 && sortedDistinctDice[3] - sortedDistinctDice[2] == 1 ||
            sortedDistinctDice.length == 5 && sortedDistinctDice[1] - sortedDistinctDice[0] == 1 && sortedDistinctDice[2] - sortedDistinctDice[1] == 1 && sortedDistinctDice[3] - sortedDistinctDice[2] == 1 ||
            sortedDistinctDice.length == 5 && sortedDistinctDice[2] - sortedDistinctDice[1] == 1 && sortedDistinctDice[3] - sortedDistinctDice[2] == 1 && sortedDistinctDice[4] - sortedDistinctDice[3] == 1) {
            return 30;
        } else {
            return 0;
        }
    }

    private int calculateLargeStraight() {
        //Sort the dice and check whether all dice are consecutive
        int[] sortedDice = IntStream.of(dice).sorted().toArray();
        if (sortedDice[1] - sortedDice[0] == 1 && sortedDice[2] - sortedDice[1] == 1 && sortedDice[3] - sortedDice[2] == 1 && sortedDice[4] - sortedDice[3] == 1) {
            return 40;
        }
        else {
            return 0;
        }
    }

    private int calculateYahtZee() {
        //Check whether all dice are equal
        if (dice[0] == dice[1] && dice[0] == dice[2] && dice[0] == dice[3] && dice[0] == dice[4]) {
            return 50;
        }
        else {
            return 0;
        }
    }

    private int calculateChance() {
        //Return the sum of all dice
        return IntStream.of(dice).sum();
    }

    public void play() throws IOException {
        //Only play if the current field is not registered yet
        if (!isRegistered[currentPlayer][currentIndex]) {

            //Set current field to the calculated score, mark field as registered and display the score as final
            score[currentPlayer][currentIndex] = currentScore;
            isRegistered[currentPlayer][currentIndex] = true;
            changeField(field[currentPlayer][currentIndex], Color.GREEN, currentScore + "");

            checkBonus();

            clearDice();

            //Display the new score, change player, and highlight the new current player
            if (currentPlayer == 0) {
                player1Score.setText(player1Name + ": " +IntStream.of(score[0]).sum());
                currentPlayer = 1;
                player1Score.setStyle(inactivePlayer);
                player2Score.setStyle(activePlayer);
            }
            else {
                player2Score.setText(player2Name + ": " +IntStream.of(score[1]).sum());
                currentPlayer = 0;
                player1Score.setStyle(activePlayer);
                player2Score.setStyle(inactivePlayer);
            }

            //Reset the turn trackers
            for (Node c : turnTrackers.getChildren()) {
                ((Circle)c).setFill(Color.TRANSPARENT);
            }

            //Set the current turn to 0, enable roll button and disable play button
            currentTurn = 0;
            btRoll.setDisable(false);
            btPlay.setDisable(true);

            //End the game if all fields are registered
            if (Stream.of(isRegistered[1]).allMatch(b -> b)) {
                endGame();
            }
        }
    }

    private void checkBonus() {
        //Bonus of 35 points should be applied if left column sums to at least 63 points.
        if (score[currentPlayer][0] + score[currentPlayer][1] + score[currentPlayer][2] + score[currentPlayer][3] + score[currentPlayer][4] + score[currentPlayer][5] >= 63) {
            score[currentPlayer][6] = 35;
            changeField(field[currentPlayer][6], Color.GREEN, "35");
        }
    }

    public void saveDice(MouseEvent e) {

        //If dice have been rolled, save or unsave the die that has been clicked
        if (dice[0] != 0) {

            //Find out to which die has been clicked by extracting the index from the id
            Die clicked = (Die)(e.getSource());
            int diceIndex = Integer.parseInt(clicked.getId().substring(1));

            //Save or unsave the die and mark appropriately
            clicked.toggleSaved();
            saveDice[diceIndex] = !saveDice[diceIndex];
        }
    }

    public void highlightField(MouseEvent e) {
        //Highlight or unhighlight selectable field on mouse hover
        Rectangle hovered = (Rectangle) (e.getSource());
        StackPane parent = (StackPane) hovered.getParent();
        Text text = (Text) parent.getChildren().get(1);
        int playerHovered = Integer.parseInt(parent.getId().substring(1, 2));
        int indexHovered = Integer.parseInt(parent.getId().substring(2));
        if (dice[0] != 0 && e.getEventType().equals(MouseEvent.MOUSE_ENTERED) && playerHovered == currentPlayer && !isRegistered[currentPlayer][indexHovered]) {
            hovered.setStroke(Color.BLUE);
        }
        else if (text.getText().equals("")) {
            hovered.setStroke(Color.GRAY);
        }
    }

    private void changeField(StackPane field, Color color, String s) {
        //Change color and content of field
        Rectangle rectangle = (Rectangle) field.getChildren().get(0);
        rectangle.setStroke(color);
        rectangle.setFill(color);
        Text text = (Text) field.getChildren().get(1);
        text.setText(s);
        text.setStyle(fieldStyle);
    }

    private void clearLastField() {
        //Clear each field that has not been registered.
        //This will clear the field that has been selected lastly but not registered
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < NUMBER_OF_FIELDS; j++) {
                if (!isRegistered[i][j]) {
                    changeField(field[i][j], Color.GRAY, "");
                }
            }
        }
    }

    private void clearDice() {
        //Set each die to 0 and remove the die image and border
        for (int i = 0; i < NUMBER_OF_DICE; i++) {
            dice[i] = 0;
            saveDice[i] = false;
        }
        dicePane.getChildren().clear();
    }

    private void endGame() throws IOException {
        //Check which player has won, disable the roll button, and show the endgame scene.
        String message;
        if (IntStream.of(score[0]).sum() > IntStream.of(score[1]).sum()) {
            message = player1Name + " wins!";
        }
        else if (IntStream.of(score[1]).sum() > IntStream.of(score[0]).sum()) {
            message = player2Name + " wins!";
        }
        else {
            message = "It's a draw!";
        }
        player1Score.setStyle(inactivePlayer);
        btRoll.setDisable(true);
        YahtzeeLauncher.getSceneManager().showEndGameScene(player1Name, player2Name, message);
     }

    public void toMenu() throws IOException {
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You will lose the current game data. Proceed?");
         alert.showAndWait();
         if (alert.getResult().equals(ButtonType.OK)) {
             YahtzeeLauncher.getSceneManager().showMenuScene();
         }
    }

    public void setup(String player1Name, String player2Name) {
        //This method initializes a new game

        //Initialize players
        this.player1Name = player1Name;
        this.player2Name = player2Name;

        player1Score = new Text(player1Name + ": 0");
        player2Score = new Text(player2Name + ": 0");

        //Name of the current player is displayed as active and the other as inactive
        player1Score.setStyle(activePlayer);
        player2Score.setStyle(inactivePlayer);

        player1Pane.getChildren().add(player1Score);
        player2Pane.getChildren().add(player2Score);

        //Add player names to the game grid as column headers
        fieldPane.add(new Text(player1Name), 1, 0);
        fieldPane.add(new Text(player2Name), 2, 0);
        fieldPane.add(new Text(player1Name), 4, 0);
        fieldPane.add(new Text(player2Name), 5, 0);

        //Initialize game fields
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < NUMBER_OF_FIELDS; j++) {
                isRegistered[i][j] = false;
                field[i][j] = new StackPane();
                Rectangle rectangle = new Rectangle(30, 30, 30, 30);
                rectangle.setStroke(Color.GRAY);
                rectangle.setFill(Color.GRAY);
                rectangle.setStrokeWidth(3);
                field[i][j].getChildren().add(rectangle);
                field[i][j].getChildren().add(new Text());
                field[i][j].setId("i" + i + "" + j);
                rectangle.setOnMouseClicked(this::select);
                rectangle.setOnMouseEntered(this::highlightField);
                rectangle.setOnMouseExited(this::highlightField);

                //The column of each field depends on which player and which half of the fields (left or right) it belongs to
                int col;
                if (i == 0 && j < NUMBER_OF_FIELDS / 2) {
                    col = 1;
                }
                else if (i == 1 && j < NUMBER_OF_FIELDS / 2) {
                    col = 2;
                }
                else if (i == 0) {
                    col = 4;
                }
                else {
                    col = 5;
                }
                fieldPane.add(field[i][j], col, j % (NUMBER_OF_FIELDS / 2) + 1);
            }
        }

        //Create three circles to track turns
        turnTrackers = new Group();
        for (int i = 0; i < 3; i++) {
            Circle c = new Circle(i * 20, 0, 8);
            c.setStroke(Color.WHITE);
            c.setFill(Color.TRANSPARENT);
            turnTrackers.getChildren().add(c);
        }
        btRoll.setGraphic(turnTrackers);
        btRoll.setContentDisplay(ContentDisplay.RIGHT);

        //Disable clicking bonus field and set its registered value to true
        field[0][6].setOnMouseClicked(null);
        field[1][6].setOnMouseClicked(null);
        isRegistered[0][6] = true;
        isRegistered[1][6] = true;

        //Disable play button
        btPlay.setDisable(true);
    }
}