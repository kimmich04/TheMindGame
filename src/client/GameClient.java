package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import game.Game;
import players.HumanPlayer;
import players.Player;

public class GameClient extends Application {
    private Game game;
    private Pane gameTable;
    private Label levelLabel;
    private double centerX;
    private double centerY;
    private HumanPlayer humanPlayer; // Declare HumanPlayer variable

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();

        // Number of players selection
        ComboBox<Integer> playerSelect = new ComboBox<>();
        playerSelect.getItems().addAll(2, 3, 4);
        playerSelect.setValue(2);

        // Start button
        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> startGame(playerSelect.getValue()));

        // Next Level button
        Button nextLevelButton = new Button("Next Level");
        nextLevelButton.setOnAction(e -> nextLevel());

        gameTable = new Pane();
        gameTable.setPrefSize(1300, 700);

        // Add components to the stage
        levelLabel = new Label();
        root.getChildren().addAll(playerSelect, startButton, nextLevelButton, levelLabel, gameTable);

        Scene scene = new Scene(root, 1500, 900);
        primaryStage.setTitle("The Mind Game Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGame(int numPlayers) {
        game = new Game(numPlayers);
        game.startLevel();
        drawGameTable_Players(numPlayers);
        //drawPlayersCard();
        updateLevelDisplay();
    }

    private void nextLevel() {
        game.nextLevel();
        game.startLevel();
        drawGameTable_Players(game.getPlayers().size());
        //drawPlayersCard();
        updateLevelDisplay();
    }

    private void updateLevelDisplay() {
        levelLabel.setText("Level: " + game.getCurrentLevel());
    }

    private void drawGameTable_Players(int numPlayers) {
        // Draw table
        double rectangleWidth = 900;
        double rectangleHeight = 500;
        Rectangle centerRectangle = new Rectangle((1500 - rectangleWidth) / 2, (900 - rectangleHeight) / 2, rectangleWidth, rectangleHeight);
        centerRectangle.setFill(Color.GREEN);
        gameTable.getChildren().add(centerRectangle);

        // Draw players
        centerX = 1500 / 2;
        centerY = 900 / 2;
        double radius = 320.0;

        for (int playerIndex = 1; playerIndex <= numPlayers; ++playerIndex) {
            Circle playerCircle = new Circle(20, Color.GREY);
            HumanPlayer humanPlayer = game.getHumanPlayer();
            int numCards = humanPlayer.getHand().size();
            double totalWidth = numCards * 35 - 5; // Width occupied by all cards, assuming 35 pixels per card with 5 pixels spacing

            if (playerIndex == 1) {
                playerCircle.setCenterX(centerX);
                playerCircle.setCenterY(centerY + (radius + 20));
                gameTable.getChildren().addAll(playerCircle);
                // humanPlayerCircle = playerCircle;

                for (int i = 0; i < numCards; ++i) {
                    Rectangle cardRect = new Rectangle(30, 50, Color.LIGHTGREY);
                    cardRect.setStroke(Color.BLACK);
                    cardRect.setX(centerX - totalWidth / 2 + i * 35); // Center the cards
                    cardRect.setY(playerCircle.getCenterY() - 170);
                    Label cardLabel = new Label(humanPlayer.getHand().get(i).toString());
                    cardLabel.setLayoutX(cardRect.getX() + 10);
                    cardLabel.setLayoutY(cardRect.getY() + 15);
                    gameTable.getChildren().addAll(cardRect, cardLabel);
                }
            } 
            else if (playerIndex == 2) {
                playerCircle.setCenterX(centerX);
                playerCircle.setCenterY(centerY - (radius + 20));
                gameTable.getChildren().addAll(playerCircle);

                for (int i = 0; i < numCards; ++i) {
                    Rectangle cardRect = new Rectangle(30, 50, Color.LIGHTGREY);
                    cardRect.setStroke(Color.BLACK);
                    cardRect.setX(centerX - totalWidth / 2 + i * 35); // Center the cards
                    cardRect.setY(playerCircle.getCenterY() + 120);
                    gameTable.getChildren().addAll(cardRect);
                }
            } 
            else if (playerIndex == 3) {
                playerCircle.setCenterX(centerX - (radius + 250));
                playerCircle.setCenterY(centerY);
                gameTable.getChildren().addAll(playerCircle);

                for (int i = 0; i < numCards; ++i) {
                    Rectangle cardRect = new Rectangle(50, 30, Color.LIGHTGREY);
                    cardRect.setStroke(Color.BLACK);
                    cardRect.setX(playerCircle.getCenterX() + 150); // Center the cards
                    cardRect.setY(centerY - totalWidth / 2 + i * 35);
                    gameTable.getChildren().addAll(cardRect);
                }
            } 
            else {
                playerCircle.setCenterX(centerX + (radius + 250));
                playerCircle.setCenterY(centerY);
                gameTable.getChildren().addAll(playerCircle);

                for (int i = 0; i < numCards; ++i) {
                    Rectangle cardRect = new Rectangle(50, 30, Color.LIGHTGREY);
                    cardRect.setStroke(Color.BLACK);
                    cardRect.setX(playerCircle.getCenterX() - 200); // Center the cards
                    cardRect.setY(centerY - totalWidth / 2 + i * 35);
                    gameTable.getChildren().addAll(cardRect);
                }
            }
        }
    }

    // private void drawPlayersCard() {
    //     int numCards = humanPlayer.getHand().size();
    //     double totalWidth = numCards * 35 - 5; // Width occupied by all cards, assuming 35 pixels per card with 5 pixels spacing

    //     for (int i = 0; i < numCards; ++i) {
    //         Rectangle cardRect = new Rectangle(30, 50, Color.WHITE);
    //         cardRect.setStroke(Color.BLACK);
    //         cardRect.setX(centerX - totalWidth / 2 + i * 35); // Center the cards
    //         cardRect.setY(humanPlayerCircle.getCenterY() - 170);
    //         Label cardLabel = new Label(humanPlayer.getHand().get(i).toString());
    //         cardLabel.setLayoutX(cardRect.getX() + 10);
    //         cardLabel.setLayoutY(cardRect.getY() + 15);
    //         gameTable.getChildren().addAll(cardRect, cardLabel);
    //     }
    // }

    public static void main(String[] args) {
        launch(args);
    }
}
