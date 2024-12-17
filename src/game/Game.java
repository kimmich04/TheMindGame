package game;

import players.BotPlayer;
import players.HumanPlayer;
import players.Player;
import java.util.*;

public class Game {
    private List<Player> players;
    private int currentLevel;

    public Game(int numPlayers) {
        players = new ArrayList<>();
        players.add(new HumanPlayer("Player"));
        for (int i = 0; i < numPlayers - 1; i++) {
            players.add(new BotPlayer("Bot " + (i + 1)));
        }
        currentLevel = 1;
    }

    public void startLevel() {
        dealCards(currentLevel);
        for (Player player : players) {
            player.playCard();
        }
    }

    private void dealCards(int numCards) {
        Random rand = new Random();
        for (Player player : players) {
            player.clearHand();
            for (int i = 0; i < numCards; i++) {
                player.addCard(rand.nextInt(100) + 1);
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
