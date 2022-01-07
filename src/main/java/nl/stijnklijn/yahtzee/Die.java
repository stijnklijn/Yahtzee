package nl.stijnklijn.yahtzee;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Die extends Pane {

    private Rectangle body;
    private int eyes;
    private boolean saved;

    public Die(int eyes, boolean saved) {
        this.body = new Rectangle(0, 0, 100, 100);
        setEyes(eyes);
        this.saved = saved;
        setMinSize(100, 100);
        paintEyes();
    }

    public void setEyes(int eyes) throws IllegalArgumentException {
        if (eyes < 1 || eyes > 6) {
            throw new IllegalArgumentException("Eyes must be between 1 and 6");
        }
        this.eyes = eyes;
    }

    private void paintEyes() {
        body.setStroke(saved ? Color.BLUE : Color.TRANSPARENT);
        body.setStrokeWidth(10);
        body.setArcHeight(20);
        body.setArcWidth(20);

        //Create a linear gradient on the body in order to suggest a shadow / 3D effect
        LinearGradient linGrad = new LinearGradient(0, 1, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.DARKRED), new Stop(1, Color.RED));
        body.setFill(linGrad);
        getChildren().add(body);

        //Paint the appropriate amount of eyes on the body
        switch(eyes) {
            case 1 -> {
                Circle c1 = createEye(50, 50);
                getChildren().add(c1);
            }
            case 2 -> {
                Circle c1 = createEye(25, 25);
                Circle c2 = createEye(75, 75);
                getChildren().addAll(c1, c2);
            }
            case 3 -> {
                Circle c1 = createEye(25, 25);
                Circle c2 = createEye(50, 50);
                Circle c3 = createEye(75, 75);
                getChildren().addAll(c1, c2, c3);
            }
            case 4 -> {
                Circle c1 = createEye(25, 25);
                Circle c2 = createEye(25, 75);
                Circle c3 = createEye(75, 25);
                Circle c4 = createEye(75, 75);
                getChildren().addAll(c1, c2, c3, c4);
            }
            case 5 -> {
                Circle c1 = createEye(25, 25);
                Circle c2 = createEye(25, 75);
                Circle c3 = createEye(50, 50);
                Circle c4 = createEye(75, 25);
                Circle c5 = createEye(75, 75);
                getChildren().addAll(c1, c2, c3, c4, c5);
            }
            case 6 -> {
                Circle c1 = createEye(25, 25);
                Circle c2 = createEye(25, 75);
                Circle c3 = createEye(25, 50);
                Circle c4 = createEye(75, 50);
                Circle c5 = createEye(75, 25);
                Circle c6 = createEye(75, 75);
                getChildren().addAll(c1, c2, c3, c4, c5, c6);
            }
        }
    }

    private Circle createEye(int x, int y) {
        Circle c = new Circle(x, y, 8);
        c.setStroke(Color.WHITE);
        c.setFill(Color.WHITE);
        return c;
    }

    public void toggleSaved() {
        body.setStroke(saved ? Color.TRANSPARENT : Color.BLUE);
        saved = !saved;
    }
}
