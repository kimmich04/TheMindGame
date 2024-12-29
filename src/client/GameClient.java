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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<Integer, Rectangle> playerCard = new HashMap<>(); // To store card rectangles 
    private Map<Integer, Label> playerCardLabel = new HashMap<>(); // To store card labels
    private Map<Integer, Rectangle> botCards = new HashMap<>(); // To store bot cards
    private Map<Integer, Label> revealedCardLabels = new HashMap<>(); // Store labels for easy removal
    private Label livesLabel;
    private Button throwingStarButton; 
    private Label throwingStarsLabel; 

    private Scene startScene, playerSelectScene, gameScene;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createStartScene();
        createPlayerSelectScene();

        primaryStage.setTitle("The Mind Game");
        primaryStage.setScene(startScene);
        Image icon = new Image(getClass().getResource("/images/TheMind_Logo.png").toString());
        primaryStage.getIcons().add(icon);
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm()); // Apply CSS file
        primaryStage.show();
    }

    private void createStartScene() {
        VBox startRoot = new VBox(20);

        Button startButton = new Button("Start Game");
        addHoverStyle(startButton);
        startButton.setOnAction(e -> primaryStage.setScene(playerSelectScene));

        Button quitButton = new Button("Quit");
        addHoverStyle(quitButton);
        quitButton.setOnAction(e -> quitGame());

        startRoot.getChildren().addAll(startButton, quitButton);

        startScene = new Scene(startRoot, 400, 300);
        addStylesheet(startScene);
    }

    private void createPlayerSelectScene() {
        VBox playerSelectRoot = new VBox(20);

        Button twoPlayersButton = new Button("2 Players");
        addHoverStyle(twoPlayersButton);
        twoPlayersButton.setOnAction(e -> switchToGameScene(2));

        Button threePlayersButton = new Button("3 Players");
        addHoverStyle(threePlayersButton);
        threePlayersButton.setOnAction(e -> switchToGameScene(3));

        Button fourPlayersButton = new Button("4 Players");
        addHoverStyle(fourPlayersButton);
        fourPlayersButton.setOnAction(e -> switchToGameScene(4));

        playerSelectRoot.getChildren().addAll(twoPlayersButton, threePlayersButton, fourPlayersButton);

        playerSelectScene = new Scene(playerSelectRoot, 400, 300);
        addStylesheet(playerSelectScene);
    }

    private void switchToGameScene(int numPlayers) {
        createGameScene(numPlayers);
        primaryStage.setScene(gameScene);
        primaryStage.setFullScreen(true); // Set the stage to full screen
        startGame(numPlayers); // Call existing startGame method
    }

    private void showSettings() {
        VBox settingsRoot = new VBox(20);

        Button resumeButton = new Button("Resume");
        addHoverStyle(resumeButton); 
        resumeButton.setOnAction(e -> primaryStage.setScene(gameScene));

        Button returnToMainMenuButton = new Button("Return to Main Menu");
        addHoverStyle(returnToMainMenuButton);
        returnToMainMenuButton.setOnAction(e -> primaryStage.setScene(startScene));

        Button quitButton = new Button("Quit");
        addHoverStyle(quitButton);
        quitButton.setOnAction(e -> quitGame());

        settingsRoot.getChildren().addAll(resumeButton, returnToMainMenuButton, quitButton);

        Scene settingsScene = new Scene(settingsRoot, 400, 300);
        addStylesheet(settingsScene);
        primaryStage.setFullScreen(true);
        primaryStage.setScene(settingsScene);
    }

    private void createGameScene(int numPlayers) {
        VBox gameRoot = new VBox(20);

        // Set up the game UI components
        gameTable = new Pane();
        gameTable.setPrefSize(1300, 700);

        levelLabel = new Label();
        livesLabel = new Label("Lives: " + numPlayers);

        // Create buttons (throwing star, play, skip, next, settings)
        throwingStarButton = new Button("Throwing Star");
        addHoverStyle(throwingStarButton);
        throwingStarButton.setOnAction(e -> useThrowingStar());

        throwingStarsLabel = new Label("Throwing Stars: 3");

        cardSelectComboBox = new ComboBox<>();

        Button playCardButton = new Button("Play Card");
        addHoverStyle(playCardButton); 
        //playCardButton.setOnAction(e -> playCard());
        playCardButton.setVisible(false); // Hide this button since we will use card click to play

        Button skipTurnButton = new Button("Skip Turn");
        addHoverStyle(skipTurnButton);
        skipTurnButton.setOnAction(e -> skipTurn());

        nextLevelButton = new Button("Next Level");
        addHoverStyle(nextLevelButton);
        nextLevelButton.setVisible(false); // Initially hidden
        nextLevelButton.setOnAction(e -> nextLevel());

        // Settings button
        Button settingsButton = new Button("Settings");
        addHoverStyle(settingsButton);
        settingsButton.setOnAction(e -> showSettings());

        // Add components to the game root
        gameRoot.getChildren().addAll(levelLabel, livesLabel, throwingStarsLabel, playCardButton, skipTurnButton, throwingStarButton, nextLevelButton, settingsButton, gameTable);
        //gameRoot.getStyleClass().add("scene-background");

        gameScene = new Scene(gameRoot, 1500, 900);
        addStylesheet(gameScene);
    }

    //EVENTS------------------------------------------------------------------------------------
    private void startGame(int numPlayers) {
        game = new Game(numPlayers, nextLevelButton);
        game.startLevel();
        drawGameTableAndPlayer(numPlayers);
        updateLevelDisplay();
        updateLivesDisplay();
        updateThrowingStarsDisplay();
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
        updateLivesDisplay();
        updateThrowingStarsDisplay();
        updateCardSelection();
        currentPlayerIndex = 0; // Start with the human player
        lastPlayedCard = 0; // Initialize with the smallest possible card value
        displayHands(); //Display all hands on console (just for testing)
    }
    
    private void playCard(Integer selectedCard) {
        if (selectedCard != null) {
            // Remove the card from the player's hand
            humanPlayer.getHand().remove(selectedCard);
            displayPlayedCard(selectedCard); // Display card in center of table
            removePlayerCard(selectedCard);
    
            lastPlayedCard = selectedCard;
    
            // Check if any player (including bots) has a smaller card in their hand and deduct a life
            boolean smallerCardsExist = removeAllSmallerCards(selectedCard);
            if (smallerCardsExist) {
                game.decreaseLives();
                System.out.println("You lost a life! Lives remaining: " + game.getLives());
                updateLivesDisplay();
            }
    
            updateCardSelection();
    
            if (humanPlayer.getHand().isEmpty()) {
                new Timeline(new KeyFrame(Duration.seconds(3), evv -> continueBotTurns())).play();
            }
        }
    }    
    
    private void skipTurn() {
        System.out.println("Skipping turn!");
        proceedToNextTurn();
    }

    private void useThrowingStar() {
        if (game.useThrowingStar()) {
            revealSmallestCards();
            updateThrowingStarsDisplay();
        } else {
            System.out.println("No throwing stars left!");
        }
    }    

    private void quitGame(){
        System.exit(0);
    }
    //EVENTS------------------------------------------------------------------------------------

    //UTILITIES---------------------------------------------------------------------------------
    
    // CSS Styling-------------------------------------------------------------------------------------------
    private void addHoverStyle(Button button) {
        button.getStyleClass().add("button-hover");
    }
    
    private void addStylesheet(Scene scene) {
        scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
    }
    // CSS Styling-------------------------------------------------------------------------------------------

    //DRAWING----------------------------------------------------------------------------------------------------------------------------------------------------------
    private void drawGameTableAndPlayer(int numPlayers) {
        // Clear previous elements
        gameTable.getChildren().clear();
    
        // Draw table
        double rectangleWidth = 900;
        double rectangleHeight = 500;
        double screenWidth = primaryStage.getWidth();
        double screenHeight = primaryStage.getHeight();
    
        // Adjust the centerY to move everything upwards
        double adjustedCenterY = screenHeight / 3; // Adjust this value to move the elements upwards
    
        // Center the rectangle
        Rectangle centerRectangle = new Rectangle((screenWidth - rectangleWidth) / 2, adjustedCenterY - (rectangleHeight / 2), rectangleWidth, rectangleHeight);
        centerRectangle.setFill(Color.GREEN);
        gameTable.getChildren().add(centerRectangle);
    
        // Draw players
        centerX = screenWidth / 2;
        centerY = adjustedCenterY;
        double radius = 320.0;
    
        for (int playerIndex = 0; playerIndex < numPlayers; ++playerIndex) {
            Player currentPlayer = game.getPlayers().get(playerIndex);
            playerCircle = new Circle(20, Color.GREY);
            int numCards = currentPlayer.getHand().size();
            double totalWidth = numCards * 35 - 5; // Width occupied by all cards, assuming 35 pixels per card with 5 pixels spacing
    
            if (currentPlayer instanceof HumanPlayer) {
                // Player
                drawPlayers(centerX, centerY + (radius + 20));
                for (int i = 0; i < numCards; ++i) {
                    drawPlayersCard(30, 50, centerX - totalWidth / 2 + i * 35, playerCircle.getCenterY() - 170, "player", i, currentPlayer);
                }
            } else if (playerIndex == 1) {
                drawPlayers(centerX, centerY - (radius + 20));
                for (int i = 0; i < numCards; ++i) {
                    drawPlayersCard(30, 50, centerX - totalWidth / 2 + i * 35, playerCircle.getCenterY() + 120, "bot", i, currentPlayer);
                }
            } else if (playerIndex == 2) {
                drawPlayers(centerX - (radius + 250), centerY);
                for (int i = 0; i < numCards; ++i) {
                    drawPlayersCard(50, 30, playerCircle.getCenterX() + 150, centerY - totalWidth / 2 + i * 35, "bot", i, currentPlayer);
                }
            } else if (playerIndex == 3) {
                drawPlayers(centerX + (radius + 250), centerY);
                for (int i = 0; i < numCards; ++i) {
                    drawPlayersCard(50, 30, playerCircle.getCenterX() - 200, centerY - totalWidth / 2 + i * 35, "bot", i, currentPlayer);
                }
            }
        }
    }    
    
    private void drawPlayers(double circleX, double circleY) {
        playerCircle.setCenterX(circleX);
        playerCircle.setCenterY(circleY);
        gameTable.getChildren().addAll(playerCircle);
    }
    
    private void drawPlayersCard(int cardX, int cardY, double centerX, double centerY, String category, int card_ith, Player currentPlayer) {
        Rectangle cardRect = new Rectangle(cardX, cardY, Color.LIGHTGREY);
        cardRect.setStroke(Color.BLACK);
        // Center the cards
        cardRect.setX(centerX);
        cardRect.setY(centerY);
    
        Integer cardValue = currentPlayer.getHand().get(card_ith);
    
        if (category.equals("bot")) {
            gameTable.getChildren().add(cardRect);
            // Save coordinates of the bots' cards
            botCards.put(cardValue, cardRect);
        } else {
            Label cardLabel = new Label(cardValue.toString());
            cardLabel.setLayoutX(cardRect.getX() + 10);
            cardLabel.setLayoutY(cardRect.getY() + 15);
            gameTable.getChildren().addAll(cardRect, cardLabel);
            // Save the coordinates of the player's cards
            playerCard.put(cardValue, cardRect);
            playerCardLabel.put(cardValue, cardLabel);

            // Add event handlers to change cursor and move card up on hover
            cardRect.setOnMouseEntered(e -> {
                cardRect.getStyleClass().add("card-hover");
                cardRect.getStyleClass().add("card-selected");
                cardLabel.getStyleClass().add("card-selected");
            });
            cardRect.setOnMouseExited(e -> {
                cardRect.getStyleClass().remove("card-hover");
                cardRect.getStyleClass().remove("card-selected");
                cardLabel.getStyleClass().remove("card-selected");
            });
            cardLabel.setOnMouseEntered(e -> {
                cardLabel.getStyleClass().add("card-hover");
                cardRect.getStyleClass().add("card-selected");
                cardLabel.getStyleClass().add("card-selected");
            });
            cardLabel.setOnMouseExited(e -> {
                cardLabel.getStyleClass().remove("card-hover");
                cardRect.getStyleClass().remove("card-selected");
                cardLabel.getStyleClass().remove("card-selected");
            });
            // Add event handler to play card on click
            cardRect.setOnMouseClicked(e -> playCard(cardValue));
            cardLabel.setOnMouseClicked(e -> playCard(cardValue));
        }
    }    
    //DRAWING----------------------------------------------------------------------------------------------------------------------------------------------------------
    
    //REMOVING----------------------------------------------------------------------------------------------------------------
    //Removing player's displayed cards
    private void removePlayerCard(int card) {
        if (playerCard.containsKey(card) && playerCardLabel.containsKey(card)) { 
            Rectangle cardRect = playerCard.get(card);
            Label cardLabel = playerCardLabel.get(card);
            gameTable.getChildren().removeAll(cardRect, cardLabel);
            playerCard.remove(card);
            playerCardLabel.remove(card);
        }
    }    
    
    //removing bots's displayed cards
    private void removeBotCard(int card) {
        if (botCards.containsKey(card)) {
            Rectangle cardRect = botCards.get(card);
            gameTable.getChildren().remove(cardRect);
            botCards.remove(card);
    
            // Remove the label if it exists
            if (revealedCardLabels.containsKey(card)) {
                Label cardLabel = revealedCardLabels.get(card);
                gameTable.getChildren().remove(cardLabel);
                revealedCardLabels.remove(card);
            }
        }
    }

    //checking is there any card smaller than played card
    private boolean removeAllSmallerCards(Integer cardToPlay) {
        boolean smallerCardsExist = false;
    
        // Check and remove smaller cards from the human player's hand
        List<Integer> smallerPlayerCards = humanPlayer.getHand().stream()
            .filter(card -> card < cardToPlay)
            .collect(Collectors.toList());
    
        if (!smallerPlayerCards.isEmpty()) {
            smallerCardsExist = true;
            for (Integer card : smallerPlayerCards) {
                humanPlayer.getHand().remove(card);
                removePlayerCard(card);
            }
            System.out.println("Removed smaller cards from player: " + smallerPlayerCards);
        }
    
        // Check and remove smaller cards from bots' hands
        for (Player player : game.getPlayers()) {
            if (player instanceof BotPlayer) {
                List<Integer> smallerBotCards = player.getHand().stream()
                    .filter(card -> card < cardToPlay)
                    .collect(Collectors.toList());
    
                if (!smallerBotCards.isEmpty()) {
                    smallerCardsExist = true;
                    for (Integer card : smallerBotCards) {
                        player.getHand().remove(card);
                        removeBotCard(card);
                    }
                    System.out.println("Removed smaller cards from " + player.getName() + ": " + smallerBotCards);
                }
            }
        }
    
        updateCardSelection();
        return smallerCardsExist;
    }
    //REMOVING----------------------------------------------------------------------------------------------------------------

    //UPDATING-----------------------------------------------------------
    private void updateLevelDisplay() {
        levelLabel.setText("Level: " + game.getCurrentLevel());
    }
    
    private void updateLivesDisplay() { 
        livesLabel.setText("Lives: " + game.getLives()); 
    }

    private void updateCardSelection() {
        cardSelectComboBox.getItems().clear();
        humanPlayer = game.getHumanPlayer();
        if (humanPlayer != null)
            cardSelectComboBox.getItems().addAll(humanPlayer.getHand());
    }

    private void updateThrowingStarsDisplay() {
        throwingStarsLabel.setText("Throwing Stars: " + game.getThrowingStars());
    }    
    //UPDATING-----------------------------------------------------------

    //DISPLAYING---------------------------------------------------------------------------------
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
    
    private void revealSmallestCards() {
        for (Player player : game.getPlayers()) {
            if (player instanceof BotPlayer) {
                List<Integer> hand = player.getHand();
                if (!hand.isEmpty()) {
                    Integer smallestCard = Collections.min(hand);
                    revealCardNumberAtPosition(smallestCard, player);
                }
            }
        }

         // Remove the card from the player's hand 
        List <Integer> humanHand = humanPlayer.getHand();
        if (!humanHand.isEmpty()){
            Integer smallestHumanCard = Collections.min(humanHand);
            humanHand.remove(smallestHumanCard);
        }
    }
    
    private void revealCardNumberAtPosition(Integer cardValue, Player player) {
        // Find the index of the card in the bot's hand
        int cardIndex = player.getHand().indexOf(cardValue);
    
        // Retrieve the corresponding rectangle
        Rectangle cardRect = botCards.get(cardValue);
    
        // Create a label to display the card value
        Label cardLabel = new Label(cardValue.toString());
    
        // Rotate labels for Bot 2 (left) and Bot 3 (right)
        if (game.getPlayers().indexOf(player) == 2) { // Assuming Bot 2 is the leftmost bot
            cardLabel.setRotate(90); // Rotate 90 degrees counterclockwise
            cardLabel.setLayoutX(cardRect.getX() + 20);
            cardLabel.setLayoutY(cardRect.getY() + 5);
        } else if (game.getPlayers().indexOf(player) == 3) { // Assuming Bot 3 is the rightmost bot
            cardLabel.setRotate(-90); // Rotate 90 degrees clockwise
            cardLabel.setLayoutX(cardRect.getX() + 15);
            cardLabel.setLayoutY(cardRect.getY() + 5);
        } else {
            cardLabel.setRotate(0); // No rotation for other players
            cardLabel.setLayoutX(cardRect.getX() + 10);
            cardLabel.setLayoutY(cardRect.getY() + 15);
        }
    
        // Add the label to the game table and store it in the map
        gameTable.getChildren().add(cardLabel);
        revealedCardLabels.put(cardValue, cardLabel);
    
        // Remove the card from the bots's hand
        player.getHand().remove(cardValue);
        botCards.remove(cardValue);
    }    
    //DISPLAYING---------------------------------------------------------------------------------

    private void proceedToNextTurn() {
        if (currentPlayerIndex == 0) {
            sortBotsByNextPlayableCard();
        }
    
        currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
    
        if (checkLevelComplete()) {
            System.out.println("Level complete");
            nextLevelButton.setVisible(true);
            return;
        }
    
        if (humanPlayer.getHand().isEmpty()) {
            new Timeline(new KeyFrame(Duration.seconds(3), ev -> continueBotTurns())).play();
            return;
        }
    
        if (currentPlayerIndex == 0) {
            System.out.println("Your turn! Current hand: " + humanPlayer.getHand());
        } else {
            Player currentPlayer = game.getPlayers().get(currentPlayerIndex);
            if (currentPlayer instanceof HumanPlayer) {
                System.out.println("Your turn! Current hand: " + humanPlayer.getHand());
            } else {
                Timeline botTurnTimeline = new Timeline(new KeyFrame(Duration.seconds(3), ev -> {
                    Integer cardToPlay = ((BotPlayer) currentPlayer).playCardAfter(lastPlayedCard);
                    lastPlayedCard = cardToPlay;
    
                    // Check if any player (including bots) has a smaller card in their hand and deduct a life
                    boolean smallerCardsExist = removeAllSmallerCards(cardToPlay);
                    if (smallerCardsExist) {
                        game.decreaseLives();
                        System.out.println("You lost a life! Lives remaining: " + game.getLives());
                        updateLivesDisplay();
                    }
    
                    removeBotCard(cardToPlay);
                    displayPlayedCard(cardToPlay);
    
                    if (game.isGameOver()) {
                        System.out.println("Game over! You have no lives left.");
                        return; // Exit the method to prevent further actions
                    }
    
                    if (checkLevelComplete()) {
                        System.out.println("Level complete");
                        nextLevelButton.setVisible(true);
                        return;
                    }
    
                    if (humanPlayer.getHand().isEmpty()) {
                        new Timeline(new KeyFrame(Duration.seconds(3), ev2 -> continueBotTurns())).play();
                        return;
                    }
    
                    // Ensure it's the player's turn next
                    currentPlayerIndex = 0;
                    System.out.println("Your turn! Current hand: " + humanPlayer.getHand());
                }));
                botTurnTimeline.setCycleCount(1);
                botTurnTimeline.play();
            }
        }
    }    
    
    private void continueBotTurns() {
        List<Player> bots = game.getPlayers().stream()
            .filter(player -> player instanceof BotPlayer)
            .sorted((bot1, bot2) -> ((BotPlayer) bot1).getNextPlayableCard(lastPlayedCard)
            .compareTo(((BotPlayer) bot2).getNextPlayableCard(lastPlayedCard)))
            .collect(Collectors.toList());
    
        playNextBotCard(bots, 0);
    }
    
    private void playNextBotCard(List<Player> bots, int index) {
        if (checkLevelComplete()) {
            System.out.println("Level complete");
            nextLevelButton.setVisible(true); // Show the "Next Level" button
            return;
        }
    
        if (index >= bots.size()) {
            return;
        }
    
        Player bot = bots.get(index);
        if (bot instanceof BotPlayer && !bot.getHand().isEmpty()) {
            Integer cardToPlay = ((BotPlayer) bot).playCardAfter(lastPlayedCard);
            lastPlayedCard = cardToPlay;
            removeBotCard(cardToPlay);
            displayPlayedCard(cardToPlay); // Display bot's card in center of table
    
            // Check if the human player has a smaller card in their hand and deduct a life
            boolean smallerCardsExist = removeAllSmallerCards(cardToPlay);
            if (smallerCardsExist) {
                game.decreaseLives();
                System.out.println("You lost a life! Lives remaining: " + game.getLives());
                updateLivesDisplay();
            }
    
            if (game.isGameOver()) {
                System.out.println("Game over! You have no lives left.");
                return; // Exit the method to prevent further actions
            }
    
            if (checkLevelComplete()) {
                System.out.println("Level complete");
                nextLevelButton.setVisible(true);
                return;
            }

            if (humanPlayer.getHand().isEmpty()) {
                new Timeline(new KeyFrame(Duration.seconds(3), ev2 -> continueBotTurns())).play();
                return;
            }
        }
    
        Timeline botTurnTimeline = new Timeline(new KeyFrame(Duration.seconds(3), ev -> {
            playNextBotCard(bots, index + 1);
        }));
        botTurnTimeline.setCycleCount(1);
        botTurnTimeline.play();
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
    //ULTILITIES---------------------------------------------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }
}    