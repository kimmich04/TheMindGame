package players;

import java.util.Random;

public class BotPlayer extends Player {
    public BotPlayer(String name) {
        super(name);
    }

    @Override
    public void playCard() {
        Random rand = new Random();
        int card = hand.remove(rand.nextInt(hand.size()));
        System.out.println(name + " plays card: " + card);
    }
}
