package proyecto;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Snake extends Circle {

    private List<Circle> tails;
    private int length = 0;
    private Direction currentDirection;
    private static final int STEP = 8;

    public Snake(double d, double d1, double d2) {
        // Cabeza de la serpiente de un color aleatorio
        super(d, d1, d2, Color.RED);
        tails = new ArrayList<>();
        currentDirection = Direction.UP;

    }

    public void step() {
        for (int i = length - 1; i >= 0; i--) {
            if (i == 0) {
                tails.get(i).setCenterX(getCenterX());
                tails.get(i).setCenterY(getCenterY());
            } else {
                tails.get(i).setCenterX(tails.get(i - 1).getCenterX());
                tails.get(i).setCenterY(tails.get(i - 1).getCenterY());
            }
        }

        if (currentDirection == Direction.UP) {
            setCenterY(getCenterY() - STEP);
        } else if (currentDirection == Direction.DOWN) {
            setCenterY(getCenterY() + STEP);
        } else if (currentDirection == Direction.LEFT) {
            setCenterX(getCenterX() - STEP);
        } else if (currentDirection == Direction.RIGHT) {
            setCenterX(getCenterX() + STEP);
        }
    }

    public boolean eatSelf() {
        for (int i = 0; i < length; i++) {
            if (this.getCenterX() == tails.get(i).getCenterX() && this.getCenterY() == tails.get(i).getCenterY()) {
                return true;
            }
        }
        return false;
    }

    public int getLength() {
        return length;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    private Circle endTail() {
        if (length == 0) {
            return this;
        }
        return tails.get(length - 1);
    }

    public void eat(Circle food, Pane pane) {
        Circle tail = endTail();
        food.setRadius(5);
        food.setCenterX(tail.getCenterX());
        food.setCenterY(tail.getCenterY());
        // hacer que el food.setFill genere un color aleatorio
        food.setFill(Color.RED);
        tails.add(length++, food);
        ArrayList<ImageView> imgs = new ArrayList<>();
        for (Node n : pane.getChildren()) {
            if (n instanceof ImageView) {
                ImageView img = (ImageView) n;
                imgs.add(img);
            }
        }
        Platform.runLater(() -> {
            pane.getChildren().removeAll(imgs);
        });
    }

}
