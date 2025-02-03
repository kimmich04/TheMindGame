package client;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

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

public class Main extends Application {
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
    private Label livesLabel, starsLabel, throwingStarsLabel, countdownLabel;
    private Button throwingStarButton, skipTurnButton; 
    private Image heartImage, throwingStarImage;

    private Scene startScene, playerSelectScene, gameScene, gameOverScene, gameCompletedScene;
    private Stage primaryStage;
    private static final String BACKGROUND_IMAGE_PATH = "/images/startBackground.jpg"; // Define the background image path

    @Override
    public void start(Stage primaryStage) {
        try {
        this.primaryStage = primaryStage;
        
        createStartScene();
        createPlayerSelectScene();
        heartImage = new Image(getClass().getResource("/images/lives.png").toExternalForm());
        throwingStarImage = new Image(getClass().getResource("/images/throwingStar.png").toExternalForm());
        primaryStage.setTitle("The Mind Game");
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        Image icon = new Image(getClass().getResource("/images/TheMind_Logo.png").toString());
        primaryStage.getIcons().add(icon);
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm()); // Apply CSS file
        primaryStage.show();
        }
        catch(Exception e) {
			e.printStackTrace();
		}
    }

    private void createStartScene() {
        VBox startRoot = new VBox(20);
        
        // Insert background image startBackground.png
        setBackground(startRoot);
        startRoot.setAlignment(Pos.CENTER_LEFT);
        
        Button startButton = new Button("Start Game");
        addHoverEffect(startButton);
        startButton.setOnAction(e -> primaryStage.setScene(playerSelectScene));
        Button quitButton = new Button("Quit");
        addHoverEffect(quitButton);
        quitButton.setOnAction(e -> quitGame());
        
        startRoot.getChildren().addAll(startButton, quitButton);

        startScene = new Scene(startRoot, 300, 400);
        addStylesheet(startScene);
    }

    private void createPlayerSelectScene() {
        VBox playerSelectRoot = new VBox(20);
        
        setBackground(playerSelectRoot);
        playerSelectRoot.setAlignment(Pos.CENTER_LEFT); // Align the VBox to center-left
        
        Button twoPlayersButton = new Button("2 Players");
        twoPlayersButton.setOnAction(e -> switchToGameScene(2));
        addHoverEffect(twoPlayersButton);

        Button threePlayersButton = new Button("3 Players");
        threePlayersButton.setOnAction(e -> switchToGameScene(3));
        addHoverEffect(threePlayersButton);

        Button fourPlayersButton = new Button("4 Players");
        fourPlayersButton.setOnAction(e -> switchToGameScene(4));
        addHoverEffect(fourPlayersButton);

        Button returnToMainMenuButton = new Button("Main Menu");
        addHoverEffect(returnToMainMenuButton);
        returnToMainMenuButton.setOnAction(e -> primaryStage.setScene(startScene));
        
        playerSelectRoot.getChildren().addAll(twoPlayersButton, threePlayersButton, fourPlayersButton, returnToMainMenuButton);
        
        playerSelectScene = new Scene(playerSelectRoot, 300, 400);
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
        
        setBackground(settingsRoot);
        settingsRoot.setAlignment(Pos.CENTER_LEFT); // Align the VBox to center-left
        
        Button resumeButton = new Button("Resume");
        addHoverEffect(resumeButton);
        resumeButton.setOnAction(e -> {
            primaryStage.setScene(gameScene);
            primaryStage.setFullScreen(true); // Set the stage to full screen
        });
        
        Button returnToMainMenuButton = new Button("Main Menu");
        addHoverEffect(returnToMainMenuButton);
        returnToMainMenuButton.setOnAction(e -> primaryStage.setScene(startScene));

        Button quitButton = new Button("Quit");
        addHoverEffect(quitButton);
        quitButton.setOnAction(e -> quitGame());

        settingsRoot.getChildren().addAll(resumeButton, returnToMainMenuButton, quitButton);

        Scene settingsScene = new Scene(settingsRoot, 300, 400);
        addStylesheet(settingsScene);
        primaryStage.setFullScreen(true);
        primaryStage.setScene(settingsScene);
    }


    private void createGameScene(int numPlayers) {
        // Create the main BorderPane container
        BorderPane gameRoot = new BorderPane();
        gameRoot.setStyle("-fx-background-color: #151515 ;"); 
        
        // Top region for the level label
        VBox levelRoot = new VBox();
        levelRoot.setAlignment(Pos.TOP_CENTER);

        // Left region for the lives, throwing stars, and buttons
        VBox buttonRoot = new VBox(20);
        buttonRoot.setAlignment(Pos.CENTER_LEFT); // Align the VBox's children to the left

        // Set up the game UI components
        gameTable = new Pane();
        gameTable.setPrefSize(1300, 700);

        levelLabel = new Label();
        levelLabel.getStyleClass().add("level-label");

        livesLabel = new Label("Lives: " + numPlayers);
        livesLabel.getStyleClass().add("game-label");

        // Create buttons (Throwing Star, Skip Turn, Next Level, Settings)
        throwingStarButton = new Button("Throwing Star");
        addHoverEffect(throwingStarButton);
        throwingStarButton.getStyleClass().add("game-button");
        throwingStarButton.setStyle("-fx-pref-width: 200px;");
        throwingStarButton.setOnAction(e -> useThrowingStar());

        throwingStarsLabel = new Label("Throwing Stars: 3");
        throwingStarsLabel.getStyleClass().add("game-label");
        cardSelectComboBox = new ComboBox<>();

        skipTurnButton = new Button("Skip Turn");
        addHoverEffect(skipTurnButton);
        skipTurnButton.getStyleClass().add("game-button");
        skipTurnButton.setOnAction(e -> skipTurn());

        nextLevelButton = new Button("Next Level");
        addHoverEffect(nextLevelButton);
        nextLevelButton.getStyleClass().add("game-button");
        nextLevelButton.setVisible(false); // Initially hidden
        nextLevelButton.setOnAction(e -> nextLevel());

        // Settings button
        Button settingsButton = new Button("Settings");
        addHoverEffect(settingsButton);
        settingsButton.getStyleClass().add("game-button");
        settingsButton.setOnAction(e -> showSettings());

        // Add components to their respective containers
        levelRoot.getChildren().add(levelLabel);
        buttonRoot.getChildren().addAll(livesLabel, throwingStarsLabel, skipTurnButton, throwingStarButton, nextLevelButton, settingsButton);
        
        // Set the layout regions:
        gameRoot.setTop(levelRoot);
        gameRoot.setLeft(buttonRoot); // Changed from setRight to setLeft
        gameRoot.setBottom(gameTable);

        gameScene = new Scene(gameRoot, 1500, 900);
        addStylesheet(gameScene);
    }

    private void createGameOverScene() {
        BorderPane gameOverRoot = new BorderPane();
        gameOverRoot.setStyle("-fx-background-color: #151515;");

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setStyle("-fx-font-size: 60px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button restartButton = new Button("Restart");
        addHoverEffect(restartButton);
        restartButton.getStyleClass().add("game-button");
        restartButton.setOnAction(e -> primaryStage.setScene(playerSelectScene));

        Button exitButton = new Button("Exit");
        addHoverEffect(exitButton);
        exitButton.getStyleClass().add("game-button");
        exitButton.setOnAction(e -> quitGame());

        centerBox.getChildren().addAll(gameOverLabel, restartButton, exitButton);
        gameOverRoot.setCenter(centerBox);

        gameOverScene = new Scene(gameOverRoot, 1500, 900);
        addStylesheet(gameOverScene);
    }

    private void createGameCompletedScene() {
        BorderPane gameCompletedRoot = new BorderPane();
        gameCompletedRoot.setStyle("-fx-background-color: #151515;");

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        Label completedLabel = new Label("Congratulations! You completed the game!");
        completedLabel.setStyle("-fx-font-size: 60px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button mainMenuButton = new Button("Main Menu");
        addHoverEffect(mainMenuButton);
        mainMenuButton.getStyleClass().add("game-button");
        mainMenuButton.setOnAction(e -> primaryStage.setScene(startScene));

        Button exitButton = new Button("Exit");
        addHoverEffect(exitButton);
        exitButton.getStyleClass().add("game-button");
        exitButton.setOnAction(e -> quitGame());

        centerBox.getChildren().addAll(completedLabel, mainMenuButton, exitButton);
        gameCompletedRoot.setCenter(centerBox);

        gameCompletedScene = new Scene(gameCompletedRoot, 1500, 900);
        addStylesheet(gameCompletedScene);
    }

    private void switchToGameOverScene() {
        createGameOverScene();
        Stage stage = (Stage) nextLevelButton.getScene().getWindow();
        stage.setScene(gameOverScene);
        stage.setFullScreen(true); // Set full screen
        stage.show();
    }

    private void switchToGameCompletedScene() {
        createGameCompletedScene();
        Stage stage = (Stage) nextLevelButton.getScene().getWindow();
        stage.setScene(gameCompletedScene);
        stage.setFullScreen(true); // Ensure full-screen mode
        stage.show();
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
    	if (game.isGameCompleted()) {
            switchToGameCompletedScene();
            return;
        }
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
            // Get the card and its label from the player's hand
            Rectangle cardRect = playerCard.get(selectedCard);
            Label cardLabel = playerCardLabel.get(selectedCard);

            // Create a new TranslateTransition for the card animation
            TranslateTransition transition = new TranslateTransition(Duration.seconds(1), cardRect);
            transition.setToX(centerX - cardRect.getX());
            transition.setToY(centerY - cardRect.getY());
            transition.setOnFinished(event -> {
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
            });

            // Play the transition
            transition.play();

            // Create a similar transition for the label
            TranslateTransition labelTransition = new TranslateTransition(Duration.seconds(1), cardLabel);
            labelTransition.setToX(centerX - cardLabel.getLayoutX());
            labelTransition.setToY(centerY - cardLabel.getLayoutY());
            labelTransition.play();

        }
    }
    
    private void skipTurn() {
        System.out.println("Skipping turn!");
        skipTurnButton.setVisible(false);
        proceedToNextTurn();
    }

    private void useThrowingStar() {
        if (game.useThrowingStar()) {
            revealSmallestCards();
            updateThrowingStarsDisplay();
            throwingStarButton.setVisible(false);

            if (checkLevelComplete()) {
                System.out.println("Level complete");
                nextLevelButton.setVisible(true);
                return;
        }
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
    // Create a reusable method to apply hover effects
    private void addHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            button.getStyleClass().add("card-hover");
            button.getStyleClass().add("cursor-entered");
        });
        button.setOnMouseExited(e -> {
            button.getStyleClass().remove("card-hover");
            button.getStyleClass().remove("cursor-entered");
        });
        button.getStyleClass().add("transparent-button");
        button.getStyleClass().add("center-left-alignment");
    }
    
    private void addStylesheet(Scene scene) {
        scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
    }
    
    private void setBackground(Pane pane) {
        BackgroundImage backgroundImage = new BackgroundImage(
            new Image(getClass().getResource(BACKGROUND_IMAGE_PATH).toExternalForm()), // Path to your image
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false)
        );
        pane.setBackground(new Background(backgroundImage));
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
                playerCircle.setFill(Color.web("#151515"));
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
        levelLabel.setText("LEVEL: " + game.getCurrentLevel());
    }
    
    private void updateLivesDisplay() {
        // Clear any existing heart images
        livesLabel.setGraphic(null);
        livesLabel.setText("");

        // Create an HBox to hold the hearts
        HBox heartBox = new HBox(5); // 5 is the spacing between hearts

        // Add heart images based on the number of lives
        for (int i = 0; i < game.getLives(); i++) {
            ImageView heartView = new ImageView(heartImage);
            heartView.setFitWidth(30); // Adjust the width as needed
            heartView.setFitHeight(30); // Adjust the height as needed
            heartBox.getChildren().add(heartView);
        }

        // Set the HBox as the graphic for the lives label
        livesLabel.setGraphic(heartBox);
    }

    private void updateCardSelection() {
        cardSelectComboBox.getItems().clear();
        humanPlayer = game.getHumanPlayer();
        if (humanPlayer != null)
            cardSelectComboBox.getItems().addAll(humanPlayer.getHand());
    }

    private void updateThrowingStarsDisplay() {
        throwingStarButton.setVisible(true);
        // Clear any existing star images
        throwingStarsLabel.setGraphic(null);
        throwingStarsLabel.setText("");
    
        // Create an HBox to hold the throwing stars
        HBox starBox = new HBox(5); // 5 is the spacing between stars
    
        // Add star images based on the number of throwing stars
        for (int i = 0; i < game.getThrowingStars(); i++) {
            ImageView starView = new ImageView(throwingStarImage);
            starView.setFitWidth(30); // Adjust the width as needed
            starView.setFitHeight(30); // Adjust the height as needed
            starBox.getChildren().add(starView);
        }
    
        // Set the HBox as the graphic for the throwing stars label
        throwingStarsLabel.setGraphic(starBox);
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
    
        // Remove the card from the human player's hand and disable events
        List<Integer> humanHand = humanPlayer.getHand();
        if (!humanHand.isEmpty()) {
            Integer smallestHumanCard = Collections.min(humanHand);
    
            // Disable cursor and moving events on the smallest human card
            if (playerCard.containsKey(smallestHumanCard)) {
                Rectangle cardRect = playerCard.get(smallestHumanCard);
                Label cardLabel = playerCardLabel.get(smallestHumanCard);
                cardRect.setOnMouseEntered(null);
                cardRect.setOnMouseExited(null);
                cardRect.setOnMouseClicked(null);
                cardLabel.setOnMouseEntered(null);
                cardLabel.setOnMouseExited(null);
                cardLabel.setOnMouseClicked(null);
            }
    
            // Remove the smallest card from the human player's hand
            humanHand.remove(smallestHumanCard);
        }
    }
    
    private void revealCardNumberAtPosition(Integer cardValue, Player player) {
        // Find the index of the card in the bot's hand
        //int cardIndex = player.getHand().indexOf(cardValue);
    
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
                    skipTurnButton.setVisible(true);

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
                        switchToGameOverScene();
                        return; // Exit the method to prevent further actions
                    }
    
                    if (checkLevelComplete()) {
                        System.out.println("Level complete");
                        nextLevelButton.setVisible(true);
                        throwingStarButton.setVisible(false);
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
            throwingStarButton.setVisible(false);
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
                switchToGameOverScene();
                return; // Exit the method to prevent further actions
            }
    
            if (checkLevelComplete()) {
                System.out.println("Level complete");
                nextLevelButton.setVisible(true);
                throwingStarButton.setVisible(false);
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
