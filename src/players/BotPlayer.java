package players;

import java.util.Optional;

public class BotPlayer extends Player {
    public BotPlayer(String name) {
        super(name);
    }

    public Integer playCardAfter(Integer lastPlayedCard) {
        Optional<Integer> cardToPlay = hand.stream().sorted().filter(card -> card > lastPlayedCard).findFirst();
//        This line uses streams to find the first playable card in the player's hand (hand).
//        It sorts the hand (sorted()) and then filters (filter()) to keep only cards greater than lastPlayedCard (playable cards based on the game's rules).
//        findFirst() retrieves the first element (card) that meets the criteria.
//        The result is stored in an Optional<Integer> container, which can be empty if no playable card is found.

        if (cardToPlay.isPresent()) {
            hand.remove(cardToPlay.get());
            System.out.println(name + " plays card: " + cardToPlay.get());
            return cardToPlay.get();
        }
        return lastPlayedCard; // Return the last played card if no valid card is found
    }
    
// Identifies the next card the bot could play, without actually playing it
    public Integer getNextPlayableCard(Integer lastPlayedCard) {
        return hand.stream().sorted().filter(card -> card > lastPlayedCard).findFirst().orElse(Integer.MAX_VALUE);
//        The code sorts the player's hand and filters for cards greater than lastPlayedCard.
//        It tries to find the first playable card using findFirst.
//        If a card is found, it returns that card's value.
//        If no playable card is found, it returns Integer.MAX_VALUE as a placeholder value (you might need to adjust this value based on your game's logic).
    }

    @Override
    public void playCard() {
        // This method can remain empty or include fallback behavior if needed
    }
}
