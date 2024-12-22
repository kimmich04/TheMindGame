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
import javafx.scene.image.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import game.Game;
import players.BotPlayer;
import players.HumanPlayer;
import players.Player;

public class GameClient extends Application {
    private Game game;
    private Pane gameTable;
    private Label levelLabel;
    private double centerX;
    private double centerY;
    private HumanPlayer humanPlayer; // Declare HumanPlayer variable
    private Circle playerCircle;
    private ComboBox<Integer> cardSelectComboBox; // ComboBox for card selection
    private int currentPlayerIndex;
    private Integer lastPlayedCard;
    private Button nextLevelButton;

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
        nextLevelButton = new Button("Next Level");
        nextLevelButton.setVisible(false); // Initially hidden
        nextLevelButton.setOnAction(e -> nextLevel());

        // Play Card button
        Button playCardButton = new Button("Play Card");
        playCardButton.setOnAction(e -> playCard());

        // Skip Turn button
        Button skipTurnButton = new Button("Skip Turn");
        skipTurnButton.setOnAction(e -> skipTurn());

        // Card selection ComboBox
        cardSelectComboBox = new ComboBox<>();

        gameTable = new Pane();
        gameTable.setPrefSize(1300, 700);

        // Add components to the stage
        levelLabel = new Label();
        root.getChildren().addAll(playerSelect, startButton, nextLevelButton, cardSelectComboBox, playCardButton, skipTurnButton, levelLabel, gameTable);

        Scene scene = new Scene(root, 1500, 900);
        primaryStage.setTitle("The Mind Game Client");
        Image icon = new Image(getClass().getResource("/images/TheMind_Logo.png").toString());
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    //events------------------------------------------------------------------------------------
    private void startGame(int numPlayers) {
        game = new Game(numPlayers, nextLevelButton);
        game.startLevel();
        drawGameTableAndPlayer(numPlayers);
        updateLevelDisplay();
        updateCardSelection();
        currentPlayerIndex = 0; // Start with the player's turn
        lastPlayedCard = 0; // Initialize with the smallest possible card value
        displayHands();
    }
    
    private void nextLevel() {
        game.nextLevel();
        game.startLevel();
        drawGameTableAndPlayer(game.getPlayers().size());
        updateLevelDisplay();
        updateCardSelection();
        currentPlayerIndex = 0; // Start with the human player
        lastPlayedCard = 0; // Initialize with the smallest possible card value
        displayHands(); //Display all hands on console (just for testing)
    }
    
    private void playCard() {
        Integer selectedCard = cardSelectComboBox.getValue();
        if (selectedCard != null) {
            // Remove the card from the player's hand
            humanPlayer.getHand().remove(selectedCard);
            lastPlayedCard = selectedCard;
            displayPlayedCard(selectedCard); // Display card in center of table
            updateCardSelection();
            proceedToNextTurn();
        }
    }    

    private void skipTurn() {
        System.out.println("Skipping turn!");
        proceedToNextTurn();
    }
    //events------------------------------------------------------------------------------------

    //utilities---------------------------------------------------------------------------------
    private void drawGameTableAndPlayer(int numPlayers) {
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
            playerCircle = new Circle(20, Color.GREY);
            humanPlayer = game.getHumanPlayer();
            int numCards = humanPlayer.getHand().size();
            double totalWidth = numCards * 35 - 5; // Width occupied by all cards, assuming 35 pixels per card with 5 pixels spacing
    
            if (playerIndex == 1) {
                // Player
                drawPlayers(centerX, centerY + (radius + 20));
                for (int i = 0; i < numCards; ++i)
                    drawPlayersCard(30, 50, centerX - totalWidth / 2 + i * 35, playerCircle.getCenterY() - 170, "player", i);                    
            } 
            else if (playerIndex == 2) {
                drawPlayers(centerX, centerY - (radius + 20));
                for (int i = 0; i < numCards; ++i) 
                    drawPlayersCard(30, 50, centerX - totalWidth / 2 + i * 35, playerCircle.getCenterY() + 120, "bot", i);
            } 
            else if (playerIndex == 3) {
                drawPlayers(centerX - (radius + 250), centerY);
                for (int i = 0; i < numCards; ++i) 
                    drawPlayersCard(50, 30, playerCircle.getCenterX() + 150, centerY - totalWidth / 2 + i * 35, "bot", i);
            } 
            else {
                drawPlayers(centerX + (radius + 250), centerY);
                for (int i = 0; i < numCards; ++i) 
                    drawPlayersCard(50, 30, playerCircle.getCenterX() - 200, centerY - totalWidth / 2 + i * 35, "bot", i);
            }
        }
    }
    
    private void drawPlayers(double circleX, double circleY) {
        playerCircle.setCenterX(circleX);
        playerCircle.setCenterY(circleY);
        gameTable.getChildren().addAll(playerCircle);
    }
    
    private void drawPlayersCard(int cardX, int cardY, double centerX, double centerY, String category, int card_ith) {
        Rectangle cardRect = new Rectangle(cardX, cardY, Color.LIGHTGREY);
        cardRect.setStroke(Color.BLACK);
        // Center the cards
        cardRect.setX(centerX); 
        cardRect.setY(centerY);
    
        if (category == "bot")
            gameTable.getChildren().addAll(cardRect);
        else {
            Label cardLabel = new Label(humanPlayer.getHand().get(card_ith).toString());
            cardLabel.setLayoutX(cardRect.getX() + 10);
            cardLabel.setLayoutY(cardRect.getY() + 15);
            gameTable.getChildren().addAll(cardRect, cardLabel);
        }
    }

    private void updateLevelDisplay() {
        levelLabel.setText("Level: " + game.getCurrentLevel());
    }
    
    private void updateCardSelection() {
        cardSelectComboBox.getItems().clear();
        humanPlayer = game.getHumanPlayer();
        if (humanPlayer != null)
            cardSelectComboBox.getItems().addAll(humanPlayer.getHand());
    }
    
    private void displayHands() {
        for (Player player : game.getPlayers()) 
            System.out.println(player.getName() + "'s hand: " + player.getHand());
    }

    private void displayPlayedCard(Integer cardValue) {
        Rectangle cardRect = new Rectangle(30, 50, Color.LIGHTGREY);
        cardRect.setStroke(Color.BLACK);
        cardRect.setX(centerX - 15);
        cardRect.setY(centerY - 25);
    
        Label cardLabel = new Label(cardValue.toString());
        cardLabel.setLayoutX(cardRect.getX() + 10);
        cardLabel.setLayoutY(cardRect.getY() + 15);
    
        gameTable.getChildren().addAll(cardRect, cardLabel);
    }

    private void proceedToNextTurn() {
        if (currentPlayerIndex == 0) {
            // Your turn ends, find the next bot's move
            sortBotsByNextPlayableCard();
        }
    
        currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
    
        if (checkLevelComplete()) {
            System.out.println("Level complete");
            nextLevelButton.setVisible(true); // Show the "Next Level" button
            return; // Level complete, exit the method
        }
    
        if (currentPlayerIndex == 0) {
            System.out.println("Your turn! Current hand: " + humanPlayer.getHand());
        } else {
            Player currentPlayer = game.getPlayers().get(currentPlayerIndex);
            if (currentPlayer instanceof HumanPlayer) {
                System.out.println("Your turn! Current hand: " + humanPlayer.getHand());
            } else {
                Timeline botTurnTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                    Integer cardToPlay = ((BotPlayer) currentPlayer).playCardAfter(lastPlayedCard);
                    lastPlayedCard = cardToPlay;
                    displayPlayedCard(cardToPlay); // Display bot's card in center of table
                    if (checkLevelComplete()) {
                        System.out.println("Level complete");
                        nextLevelButton.setVisible(true); // Show the "Next Level" button
                        return; // Level complete, exit the method
                    }
                    currentPlayerIndex = 0; // Return turn to player after each bot move
                    System.out.println("Your turn! Current hand: " + humanPlayer.getHand());
                }));
                botTurnTimeline.setCycleCount(1);
                botTurnTimeline.play();
            }
        }
    }
    
    private boolean checkLevelComplete() {
        return game.getPlayers().stream().allMatch(player -> player.getHand().isEmpty());
    }
    
    private void sortBotsByNextPlayableCard() {
        game.getPlayers().sort((player1, player2) -> {
            if (player1 instanceof BotPlayer && player2 instanceof BotPlayer) {
                Integer nextCard1 = ((BotPlayer) player1).getNextPlayableCard(lastPlayedCard);
                Integer nextCard2 = ((BotPlayer) player2).getNextPlayableCard(lastPlayedCard);
                return Integer.compare(nextCard1, nextCard2);
            }
            return 0;
        });
    }
    //utilities---------------------------------------------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }
}    