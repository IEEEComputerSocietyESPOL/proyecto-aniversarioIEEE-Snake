package proyecto;

import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class SnakeGame extends Application {
    // Ajuste de la ventana
    private static final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    private static final int PREFERRED_HEIGHT = (int) screenBounds.getHeight() - 100;
    private static final int PREFERRED_WIDTH = PREFERRED_HEIGHT + 100;
    // Longitud inicial de la serpiente
    private static final int INIT_LENGTH = 5;
    // Paneles para la GUI
    private static Scene scene = new Scene(new Pane());
    private static Pane game; // Juego
    private static VBox mainMenu; // Menú principal
    private Text score; // Puntuación del juego
    private Text temporizador; // Temporizador
    private Timeline timeline = new Timeline();
    private Food food; // Comida
    private int segundos = 0; // Segundos del temporizador
    private Random random; // Control de aleatoriedad
    private Snake snake; // Serpiente
    private static int modificadorVelocidad = 10; // Aumento de velocidad de la serpiente
    private Thread jugabilidad; // Hilo de ejecución del juego
    private HistorialPuntajes historialPuntajes; // Manejar el historial de puntajes
    /*
     * Agregar las imágenes de la comida en la carpeta resources/proyecto con la
     * extensión correspondiente
     */
    private String images[] = { "AP.png", "brownie.png",
            "empanada.png", "encebollado.png", "menestra2.png", "pastelpn.png", "sanduchepng.png" };

    private void newFood() {
        int posX = random.nextInt(PREFERRED_WIDTH);
        int posY = random.nextInt(PREFERRED_HEIGHT);
        String imageString = images[random.nextInt(images.length)];
        food = new Food(posX, posY, 0, imageString);
        Image image = new Image(getClass().getResourceAsStream(imageString));
        ImageView foodImage = new ImageView(image);
        foodImage.setFitWidth(25);
        foodImage.setFitHeight(25);
        foodImage.relocate(posX - 10, posY - 10); // Centrar la imagen en la comida
        game.getChildren().addAll(food, foodImage);
    }

    private void newSnake() {
        snake = new Snake(PREFERRED_WIDTH / 2, PREFERRED_HEIGHT / 2, 10); // d2 es el tamaño de la cabeza
        game.getChildren().add(snake);
        for (int i = 0; i < INIT_LENGTH; i++) { // Agregar la cola
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
                modificadorVelocidad = food.getImage().equals("brownie.png") ? 5 : 10;
                score.setText("Score: " + (snake.getLength() - INIT_LENGTH));
                newFood();
            }
            if (gameOver()) {
                jugabilidad.interrupt(); // detiene el juego
                timeline.stop(); // detiene el temporizador
                Platform.runLater(() -> {

                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Game Over");
                    dialog.setHeaderText("Puntaje Obtenido: " + (snake.getLength() - INIT_LENGTH));
                    dialog.setContentText("Jugador:");
                    Optional<String> name = dialog.showAndWait();

                    if (name.isPresent() && (name.get() != null && !name.get().isEmpty())) {
                        Puntaje actual = new Puntaje(name.get(), (snake.getLength() - INIT_LENGTH));
                        List<Puntaje> puntajes = historialPuntajes.getPuntajes();
                        if (historialPuntajes.getPuntajes().contains(actual)) {
                            Puntaje anterior = puntajes.get(puntajes.indexOf(actual));
                            // Si el jugador existe y su puntaje actual es mayor, se actualiza el puntaje
                            if (anterior.getPuntaje() < actual.getPuntaje()) {
                                historialPuntajes.getPuntajes().remove(anterior);
                                historialPuntajes.agregarPuntaje(actual);
                                historialPuntajes.guardarPuntajes();
                            }
                            // Si es un jugador nuevo, agrega directamente al archivo
                        } else {
                            historialPuntajes.agregarPuntaje(actual);
                            historialPuntajes.guardarPuntajes();
                        }
                    }
                    scene.setRoot(menu());
                });
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
            snake.setCenterY(PREFERRED_HEIGHT);
        } else if (snake.getCenterY() > PREFERRED_HEIGHT) {
            snake.setCenterY(0);
        }
    }

    private Pane game() {
        game = new Pane();
        game.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        // Agregamos la imagen para el fondo del juego
        Image image = new Image(getClass().getResourceAsStream("Background2.jpg"));
        BackgroundImage fondo = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(PREFERRED_WIDTH, PREFERRED_HEIGHT, false, false, false, false));
        game.setBackground(new Background(fondo));

        random = new Random();
        newSnake();
        newFood();

        // Configuración de puntaje y tiempo transcurrido
        score = new Text(PREFERRED_WIDTH / 2, 32, "Score: 0");
        score.setFont(Font.font(30));
        score.setStyle("-fx-font-weight: bold");
        score.setFill(Color.WHITE);
        score.setTextAlignment(TextAlignment.CENTER);

        temporizador = new Text(PREFERRED_WIDTH / 2 - 5, 64, "00:00:00");
        temporizador.setFont(Font.font(30));
        temporizador.setStyle("-fx-font-weight: bold");
        temporizador.setFill(Color.WHITE);
        temporizador.setTextAlignment(TextAlignment.CENTER);
        segundos = 0;

        // Creamos el Timeline para el funcionamiento del temporizador.
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            segundos++;
            temporizador.setText(segundosAtiempo(segundos));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        // Iniciamos el Timeline
        timeline.play();

        game.getChildren().addAll(score, temporizador);

        // Hilo de ejecución del juego
        Runnable r = () -> {
            try {
                for (;;) {
                    move();
                    int puntos = snake.getLength() - INIT_LENGTH;
                    // Aumentar la velocidad de la serpiente cada modificadorVelocidad puntos
                    Thread.sleep(100 / (1 + (puntos / modificadorVelocidad)));
                }
            } catch (InterruptedException ie) {
            }
        };

        // Controlador de movimiento
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

        jugabilidad = new Thread(r);
        jugabilidad.setDaemon(true);
        jugabilidad.start();
        return game;
    }

    // Crear una nueva ventana emergente para mostrar los puntajes
    private void showPuntajesWindow() {
        Stage puntajesStage = new Stage();
        puntajesStage.initModality(Modality.WINDOW_MODAL);
        puntajesStage.setTitle("Tabla de Puntajes");
        puntajesStage.setResizable(false);
        VBox puntajesLayout = new VBox();
        puntajesLayout.setSpacing(10);
        puntajesLayout.setPadding(new Insets(10));
        TableView<Puntaje> tableView = obtenerTabla();
        puntajesLayout.getChildren().addAll(tableView);
        Scene puntajesScene = new Scene(puntajesLayout);
        puntajesStage.setScene(puntajesScene);
        puntajesStage.showAndWait();
    }

    private TableView<Puntaje> obtenerTabla() {
        TableView<Puntaje> tableView = new TableView<>();
        tableView.setPrefWidth(300);
        TableColumn<Puntaje, String> playerNameColumn = new TableColumn<>("Nombre");
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Puntaje, Integer> scoreColumn = new TableColumn<>("Puntuación");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("puntaje"));
        tableView.getColumns().addAll(playerNameColumn, scoreColumn);
        int lastIndex = historialPuntajes.getPuntajes().size() >= 10 ? 10 : historialPuntajes.getPuntajes().size();
        tableView.getItems().addAll(historialPuntajes.getPuntajes().subList(0, lastIndex));
        return tableView;
    }

    private Pane menu() {
        mainMenu = new VBox();
        mainMenu.setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        mainMenu.getChildren().add(new Text("Menú Principal"));
        Button scores = new Button("Puntajes");
        scores.setDefaultButton(false);
        scores.setOnMouseClicked(eh -> showPuntajesWindow());
        Button start = new Button("Comenzar");
        start.setDefaultButton(true);
        start.setOnMouseClicked(eh -> scene.setRoot(game()));
        mainMenu.getChildren().addAll(scores, start);
        return mainMenu;
    }

    // Configuración para el temporizador
    private String segundosAtiempo(int segundos) {
        int hora = segundos / 3600;
        int minuto = (segundos % 3600) / 60;
        int segundo = segundos % 60;

        return String.format("%02d:%02d:%02d", hora, minuto, segundo);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Snake Game!");
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
