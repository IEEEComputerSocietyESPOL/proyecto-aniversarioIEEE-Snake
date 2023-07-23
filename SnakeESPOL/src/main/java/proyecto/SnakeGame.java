package proyecto;

import java.util.ArrayList;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
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
import javafx.util.Duration;

public class SnakeGame extends Application {
    private static final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    private static final int PREFERRED_HIGHT = (int) screenBounds.getHeight() - 100;
    private static final int PREFERRED_WIDTH = PREFERRED_HIGHT + 100;
    private static final int INIT_LENGTH = 5;
    private static Scene scene = new Scene(new Pane());
    private static Pane game;
    private static VBox mainMenu;
    private static VBox scores;
    private int segundos = 0;
    private Text score;
    private Text temporizador = new Text("00:00:00");
    private Circle food;
    private Random random;
    private Snake snake;
    // Agregar las imágenes de la comida en la carpeta resources/proyecto con la
    // extensión correspondiente
    private String images[] = { "apple.png", "golden-apple.png" };

    private void newFood() {
        int posX = random.nextInt(PREFERRED_WIDTH);
        int posY = random.nextInt(PREFERRED_HIGHT);
        food = new Circle(posX, posY, 0);
        Image image = new Image(getClass().getResourceAsStream(images[random.nextInt(images.length)]));
        ImageView img = new ImageView(image);
        img.setFitWidth(20);
        img.setFitHeight(20);
        img.relocate(posX - 10, posY - 10);
        game.getChildren().add(food);
        game.getChildren().add(img);
    }

    private void newSnake() {
        snake = new Snake(PREFERRED_WIDTH / 2, PREFERRED_HIGHT / 2, 7);
        game.getChildren().add(snake);
        for (int i = 0; i < INIT_LENGTH; i++) {
            newFood();
            snake.eat(food, game);

        }
    }

    private boolean hit() {
        return food.intersects(snake.getBoundsInLocal());
    }

    private boolean gameOver() {
        return snake.eatSelf();
    }

    private void move() {
        Platform.runLater(() -> {
            snake.step();
            adjustLocation();
            if (hit()) {
                snake.eat(food, game);
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
        
        // Creamos el Timeline para el funcionamiento del temporizador.
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    segundos++;
                    temporizador.setText(segundosAtiempo(segundos));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        // Iniciamos el Timeline
        timeline.play();

        game.getChildren().add(temporizador);

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

    private String segundosAtiempo(int segundos){
        int hora = segundos / 3600;
        int minuto = (segundos % 3600) / 60;
        int segundo = segundos % 60;

        return String.format("%02d:%02d:%02d", hora, minuto, segundo);
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
