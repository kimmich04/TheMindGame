package players;

public class HumanPlayer extends Player {
    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public void playCard() {
        System.out.println(name + "'s turn: " + hand);
    }
}
