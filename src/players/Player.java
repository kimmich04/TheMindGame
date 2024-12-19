package players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Player {
    protected List<Integer> hand;
    protected String name;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void addCard(int card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public void sortHand() {
        Collections.sort(hand);
    }

    public List<Integer> getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }
    
    public void setHand(List<Integer> hand) {
        this.hand = hand;
    }
    
    public abstract void playCard();
}
