package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.control.Button;
import players.Player;
import players.HumanPlayer;
import players.BotPlayer;


public class Game {
	private List<Player> players;
    private int currentLevel;
    private int maxLevel;
    private int lives;
    private int throwingStars; // Track throwing stars

    private Button nextLevelButton;

    public Game(int numPlayers, Button nextLevelButton) {
        players = new ArrayList<>();
        players.add(new HumanPlayer("Player"));
        for (int i = 0; i < numPlayers - 1; i++) {
        	players.add(new BotPlayer("Bot " + (i + 1)));
        }
        currentLevel = 1;
        this.nextLevelButton = nextLevelButton;

        if (numPlayers == 2) 
            maxLevel = 12;
        else if (numPlayers == 3) 
            maxLevel = 10;
        else if (numPlayers == 4) 
            maxLevel = 8;

        lives = numPlayers;
        throwingStars = 1; // Initialize throwing stars to 1
    }

    public int getLives() {
        return lives;
    }

    public int getThrowingStars() {
        return throwingStars;
    }

    public void decreaseLives() {
        if (lives > 0) {
            lives--;
        }
    }

    public boolean useThrowingStar() {
        if (throwingStars > 0) {
            throwingStars--;
            return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public boolean isGameCompleted() {
		if (currentLevel > maxLevel)
			return true;
		return false;
	}
    
    public void startLevel() {
        nextLevelButton.setVisible(false);
        dealCards(currentLevel);
        for (Player player : players) {
            player.playCard();
        }
    }

    private void dealCards(int numCards) {
        int totalCards = 100; // Total number of unique cards (1 to 100)
        boolean[] dealtCards = new boolean[totalCards + 1]; // Array to mark dealt cards
        Random rand = new Random();
    
        for (Player player : players) {
            player.clearHand();
            for (int i = 0; i < numCards; i++) {
                int card;
                do {
                    card = rand.nextInt(100) + 1;
                } while (dealtCards[card]); // Repeat if the card is already dealt
    
                dealtCards[card] = true; // Mark the card as dealt
                player.addCard(card);
            }
            player.sortHand();
        }
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }

    public void nextLevel() {
        if (currentLevel <= maxLevel) {
            currentLevel++;
            startLevel();
        } 
        else {
            System.out.println("Game complete! You have finished all levels.");
        }

        if (currentLevel == 2 || currentLevel == 5 || currentLevel == 8) ++throwingStars;
        else if (currentLevel == 3 || currentLevel == 6 || currentLevel == 9) ++lives;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public HumanPlayer getHumanPlayer() {
        for (Player player : players) {
            if (player instanceof HumanPlayer) {
                return (HumanPlayer) player;
            }
        }
        return null;
    }

}
