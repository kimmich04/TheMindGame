package players;

import java.util.Optional;

public class BotPlayer extends Player {
    public BotPlayer(String name) {
        super(name);
    }

    public Integer playCardAfter(Integer lastPlayedCard) {
        Optional<Integer> cardToPlay = hand.stream().sorted().filter(card -> card > lastPlayedCard).findFirst();
        if (cardToPlay.isPresent()) {
            hand.remove(cardToPlay.get());
            System.out.println(name + " plays card: " + cardToPlay.get());
            return cardToPlay.get();
        }
        return lastPlayedCard; // Return the last played card if no valid card is found
    }

    public Integer getNextPlayableCard(Integer lastPlayedCard) {
        return hand.stream().sorted().filter(card -> card > lastPlayedCard).findFirst().orElse(Integer.MAX_VALUE);
    }

    @Override
    public void playCard() {
        // This method can remain empty or include fallback behavior if needed
    }
}
