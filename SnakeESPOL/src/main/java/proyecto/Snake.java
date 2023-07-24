package proyecto;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.css.converter.PaintConverter;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Snake extends Circle {

    private List<Circle> tails;
    private int length = 0;
    private Direction currentDirection;
    private static final int STEP = 8;
    private static Color parTail = new Color(0, 0, 0, 1);
    private static Color oddTail = new Color(0, 0, 0, 1);

    public Snake(double d, double d1, double d2) {
        // Cabeza de la serpiente de un color aleatorio
        super(d, d1, d2);
        tails = new ArrayList<>();
        currentDirection = Direction.UP;
        int r = (int) (Math.random() * 255);
        int g = 255;
        int b = (int) (Math.random() * 255);
        while (g > r && g > b) {
            g = (int) (Math.random() * 255);
        }
        parTail = Color.rgb(r, g, b);
        r = (int) (Math.random() * 255);
        g = 255;
        b = (int) (Math.random() * 255);
        while (g > r && g > b) {
            g = (int) (Math.random() * 255);
        }
        oddTail = Color.rgb(r, g, b);
        this.setFill(oddTail);
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

    public void eat(Food food, Pane pane) {
        Circle tail = endTail();
        // El cuerpo de la serpiente siempre es 2 pixeles mas pequeño que la cabeza
        food.setRadius(this.getRadius() - 2);
        food.setCenterX(tail.getCenterX());
        food.setCenterY(tail.getCenterY());
        // hacer que el food.setFill genere un color aleatorio
        food.setFill(length % 2 == 0 ? parTail : oddTail);
        tails.add(length++, food);
        // Eliminar todas las imágenes de comida de la pantalla
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
