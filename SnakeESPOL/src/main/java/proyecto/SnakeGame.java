package proyecto;

import java.util.ArrayList;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.scene.control.TextInputDialog;
import java.util.Optional;

/**
 *
 * @author Amanuel 
 */
public class SnakeGame extends Application {

    private static final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    private static final int PREFERRED_HIGHT = (int) screenBounds.getHeight() - 100;
    private static final int PREFERRED_WIDTH = PREFERRED_HIGHT + 100;
    private static final int RADIUS = 5;
    private static final int INIT_LENGTH = 5;
    private static Scene scene = new Scene(new Pane());
    private static Pane game;
    private static VBox mainMenu;
    private static VBox scores;
    private Text score;
    private Circle food;
    private Random random;
    private Snake snake;

    private void newFood() {
        food = new Circle(random.nextInt(PREFERRED_WIDTH), random.nextInt(PREFERRED_HIGHT), RADIUS);
        food.setFill(Color.RED);
        game.getChildren().add(food);
    }

    private void newSnake() {
        snake = new Snake(PREFERRED_WIDTH / 2, PREFERRED_HIGHT / 2, RADIUS + 2);
        game.getChildren().add(snake);
        for (int i = 0; i < INIT_LENGTH; i++) {
            newFood();
            snake.eat(food);
        }
    }

    private boolean hit() {
        return food.intersects(snake.getBoundsInLocal());
    }

    private boolean gameOver() {
        if (hit()) {
            game.getChildren().clear();
            TextInputDialog dialog = new TextInputDialog("Ingrese su nombre");
            dialog.setTitle("Game Over");
            dialog.setHeaderText("Has perdido");
            dialog.setContentText("Por favor, ingrese su nombre:");

            Optional<String> name = dialog.showAndWait();
            menu();
            if (name.isPresent()) {
                score.setText("Game Over " + (snake.getLength() - INIT_LENGTH) + " - " + name.get());
            }
            newSnake();
            newFood();
            
        } else if (snake.eatSelf()) {
            game.getChildren().clear();
            TextInputDialog dialog = new TextInputDialog("Ingrese su nombre");
            dialog.setTitle("Game Over");
            dialog.setHeaderText("Has perdido");
            dialog.setContentText("Por favor, ingrese su nombre:");

            Optional<String> name = dialog.showAndWait();
            menu();
            if (name.isPresent()) {
                score.setText("Game Over " + (snake.getLength() - INIT_LENGTH) + " - " + name.get());
            }
            newSnake();
            newFood();
            
        }
        return snake.eatSelf();
    }

    private void move() {
        Platform.runLater(() -> {
            snake.step();
            adjustLocation();
            if (hit()) {
                snake.eat(food);
                score.setText("Score: " + (snake.getLength() - INIT_LENGTH));
                newFood();
            } else if (gameOver()) {
                game.getChildren().clear();
                game.getChildren().add(score);
                score.setText("Game Over " + (snake.getLength() - INIT_LENGTH));
                newSnake();
                newFood();
            }
        });
    }

    private void adjustLocation() {
        if (snake.getCenterX() < 0) {
            snake.setCenterX(PREFERRED_WIDTH);
        } else if (snake.getCenterX() > PREFERRED_WIDTH) {
            snake.setCenterX(0);
        }
        if (snake.getCenterY() < 0) {
            snake.setCenterY(PREFERRED_HIGHT);
        } else if (snake.getCenterY() > PREFERRED_HIGHT) {
            snake.setCenterY(0);
        }
    }

    private Pane game() {
        game = new Pane();
        game.setPrefSize(PREFERRED_WIDTH, PREFERRED_HIGHT);
        random = new Random();

        newSnake();
        newFood();

        score = new Text(0, 32, "Score: 0");
        score.setFont(Font.font(25));
        game.getChildren().add(score);

        Runnable r = () -> {
            try {
                for (;;) {
                    move();
                    Thread.sleep(100 / (1 + (snake.getLength() / 10)));
                }
            } catch (InterruptedException ie) {
            }
        };

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.UP || code == KeyCode.W) {
                snake.setCurrentDirection(Direction.UP);
            } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                snake.setCurrentDirection(Direction.DOWN);
            } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                snake.setCurrentDirection(Direction.LEFT);
            } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                snake.setCurrentDirection(Direction.RIGHT);
            }

        });

        Thread th = new Thread(r);
        th.setDaemon(true);
        th.start();
        return game;
    }

    private Pane scores() {
        scores = new VBox();
        scores.setPrefSize(PREFERRED_WIDTH, PREFERRED_HIGHT);
        scores.getChildren().add(new Text("Puntajes"));
        Button back = new Button("Volver");
        back.setOnMouseClicked(eh -> {
            scene.setRoot(menu());
        });
        scores.getChildren().add(back);
        return scores;
    }

    private Pane menu() {
        mainMenu = new VBox();
        mainMenu.setPrefSize(PREFERRED_WIDTH, PREFERRED_HIGHT);
        mainMenu.getChildren().add(new Text("Menú Principal"));
        Button scores = new Button("Puntajes");
        scores.setOnMouseClicked(eh -> {
            scene.setRoot(scores());
        });
        Button start = new Button("Comenzar");
        start.setOnMouseClicked(eh -> {
            scene.setRoot(game());
        });
        mainMenu.getChildren().addAll(scores, start);
        return mainMenu;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Snake Game!");
        scene.setRoot(menu());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}