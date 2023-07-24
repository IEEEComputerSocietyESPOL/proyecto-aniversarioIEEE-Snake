package proyecto;

import javafx.scene.shape.Circle;

public class Food extends Circle {
    private String image;

    public Food(double x, double y, double radio, String image) {
        super(x, y, radio);
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
