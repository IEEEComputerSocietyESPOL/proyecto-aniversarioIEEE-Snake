package proyecto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
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
    private Text temporizador = new Text(0, 64, "00:00:00");
    private Circle food;
    private Random random;
    private Snake snake;

    // Instancia de la clase HistorialPuntajes
    private HistorialPuntajes historialPuntajes;

    // Lista de puntajes

    private List<Puntaje> puntajes;

    // Agregar las imágenes de la comida en la carpeta resources/proyecto con la
    // extensión correspondiente
    private String images[] = { "apple.png", "golden-apple.png", "AP.png", "brownie.png",
            "empanada.png", "encebollado.png", "menestra2.png", "pastelpn.png", "sanduchepng.png" };

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

                // Método para guardar puntajes cuando sea necesario
                historialPuntajes.agregarPuntaje(new Puntaje("otro", snake.getLength() - INIT_LENGTH));
                historialPuntajes.guardarPuntajes();
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
        score.setFill(Color.WHITE);
        temporizador.setFont(Font.font(15));
        temporizador.setFill(Color.WHITE);
        game.getChildren().add(score);

        // Agregamos la imagen para el fondo del juego.
        Image image = new Image(getClass().getResourceAsStream("Background2.jpg"));
        BackgroundImage fondo = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(PREFERRED_WIDTH, PREFERRED_HIGHT, false, false, false, false));
        game.setBackground(new Background(fondo));

        // Creamos el Timeline para el funcionamiento del temporizador.
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    segundos++;
                    temporizador.setText(segundosAtiempo(segundos));
                }),
                new KeyFrame(Duration.seconds(1)));
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

        // Crear la tabla de puntuaciones
        TableView<Puntaje> tableView = new TableView<>();
        tableView.setPrefWidth(300);

        TableColumn<Puntaje, String> playerNameColumn = new TableColumn<>("Nombre");
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Puntaje, Integer> scoreColumn = new TableColumn<>("Puntuación");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("puntaje"));

        tableView.getColumns().addAll(playerNameColumn, scoreColumn);
        tableView.setItems(FXCollections.observableArrayList(puntajes));

        scores.getChildren().add(tableView);
        return scores;
    }

    private void showPuntajesWindow() {
        // Crear una nueva ventana emergente para mostrar los puntajes
        Stage puntajesStage = new Stage();
        puntajesStage.initModality(Modality.APPLICATION_MODAL);
        puntajesStage.setTitle("Tabla de Puntajes");

        // Crear el contenido de la ventana emergente (el TableView con los puntajes)
        VBox puntajesLayout = new VBox();
        puntajesLayout.setSpacing(10);
        puntajesLayout.setPadding(new Insets(10));
        TableView<Puntaje> tableView = createPuntajesTableView();
        puntajesLayout.getChildren().addAll(tableView);

        // Crear la escena y mostrar la ventana emergente
        Scene puntajesScene = new Scene(puntajesLayout);
        puntajesStage.setScene(puntajesScene);
        puntajesStage.showAndWait();
    }

    private TableView<Puntaje> createPuntajesTableView() {
        TableView<Puntaje> tableView = new TableView<>();
        tableView.setPrefWidth(300);

        TableColumn<Puntaje, String> playerNameColumn = new TableColumn<>("Nombre");
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Puntaje, Integer> scoreColumn = new TableColumn<>("Puntuación");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("puntaje"));

        tableView.getColumns().addAll(playerNameColumn, scoreColumn);
        // tableView.setItems(FXCollections.observableArrayList(puntajes));
        tableView.getItems().addAll(historialPuntajes.getPuntajes());
        return tableView;
    }

    private Pane menu() {
        mainMenu = new VBox();
        mainMenu.setPrefSize(PREFERRED_WIDTH, PREFERRED_HIGHT);
        mainMenu.getChildren().add(new Text("Menú Principal"));
        Button scores = new Button("Puntajes");
        scores.setOnMouseClicked(eh -> {
            showPuntajesWindow();
            // scene.setRoot(scores());
        });
        Button start = new Button("Comenzar");
        start.setOnMouseClicked(eh -> {
            scene.setRoot(game());
        });
        mainMenu.getChildren().addAll(scores, start);
        return mainMenu;
    }

    private String segundosAtiempo(int segundos) {
        int hora = segundos / 3600;
        int minuto = (segundos % 3600) / 60;
        int segundo = segundos % 60;

        return String.format("%02d:%02d:%02d", hora, minuto, segundo);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Snake Game!");

        // Inicializar el objeto HistorialPuntajes
        historialPuntajes = new HistorialPuntajes("puntajes.csv");
        historialPuntajes.cargarPuntajes();

        scene.setRoot(menu());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
