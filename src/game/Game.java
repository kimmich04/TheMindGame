package game;

import players.BotPlayer;
import players.HumanPlayer;
import players.Player;
import java.util.*;

import javafx.scene.control.Button;

public class Game {
    private List<Player> players;
    private int currentLevel;

    private Button nextLevelButton;

    public Game(int numPlayers, Button nextLevelButton) {
        players = new ArrayList<>();
        players.add(new HumanPlayer("Player"));
        for (int i = 0; i < numPlayers - 1; i++) {
            players.add(new BotPlayer("Bot " + (i + 1)));
        }
        currentLevel = 1;
        this.nextLevelButton = nextLevelButton;
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
        currentLevel++;
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
