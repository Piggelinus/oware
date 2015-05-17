import java.io.InputStream;
import java.io.PrintStream;

import java.util.*;

/**
 * PLayer objects represent players in the game: they select their move based on the Board they are given and which side of the board they are playing. They may quit the game before a result is decided. Results are decided by the {@link Game}.
 *
 * You need to provide two implementations of this interface, one called HumanPlayer, which acts under control of the user, and one called ComputerPlayer which plays automatically and as well as it can within the time constraints.
 *
 * Input from the human players and output to the players can be redirected with (@see in) and (@see out).
 *
 * A default contructor with no parameters is required for each imeplementation of the interface. By default the input should be set to System.in and the output stream to System.out
 *
 * @author Linus Ericsson
 * @version 0.1
 */

public class RandomPlayer implements Player {
    
    private InputStream in = System.in;
    private PrintStream out = System.out;
    
    private String name;
    private int myPlayerNum;
    private Random r = new Random();
    private int LOOKAHEAD_DEPTH = 3;
    
    public static int NUMBER_OF_PLAYERS = 2;
    public static int NUMBER_OF_HOUSES = 12;
    public static int SEEDS_IN_HOUSES = 4;
    
    /**
     *
     * Computer player moves should take less than one second to complete the getMove method or they forfeit the game.
     *
     * @param b An copy of the game that the player may experiment with. It should be a copy of the game so that the computer cannot cheat and experiments do not affect game play.
     *
     * @param playerNum
     * the number of the player: 1 or 2.
     *
     * @return the position of the house selected (counting anti-clockwise): a value in the range 1..6
     *
     * @throws QuitGameException if, instead of choosing a house, a human player chooses to quit by entering 'QUIT'. If a computer player throws QuitGameException they forfeit the game (@see #isComputer()).
     *
     **/
    
    public static void main(String[] args) {
        try {
            RandomPlayer AI = new RandomPlayer("AI");
            Board board = new BoardImpl();
            GameManagerImpl.out.println(board);
            board.makeMove(AI.getMove(board, 1), 1);
            GameManagerImpl.out.println(board);
        }
        catch (Exception e) {
            GameManagerImpl.out.println("m");
        }
    }
    
    public RandomPlayer() {
        this.name = "computer";
    }
    
    public RandomPlayer(String name) {
        this.name = name;
    }
    
    public RandomPlayer(int difficulty) {
        this.name = "computer";
        LOOKAHEAD_DEPTH = difficulty;
    }
    
    public RandomPlayer(String name, int difficulty) {
        this.name = name;
        LOOKAHEAD_DEPTH = difficulty;
    }
    
    public String getName() {
        return name;
    }
    
    public int getMove(Board b, int playerNum) throws QuitGameException {
        ArrayList<Integer> possibleMoves = getPossibleMoves(b, playerNum);
        return possibleMoves.get(r.nextInt(possibleMoves.size()));
    }
    
    public int checkMove(Board b, int house, int playerNum) throws InvalidHouseException, InvalidMoveException {
        b.makeMove(house, playerNum);
        int enemySeeds = 0;
        for (int i = 1; i <= NUMBER_OF_HOUSES / 2; i++) {
            enemySeeds += b.getSeeds(i, (playerNum % 2) + 1);
        }
        if (enemySeeds <= 0) {
            throw new InvalidMoveException("Invalid input: must sow seeds on opponents side");
        }
        else {
            return house;
        }
    }
    
    public ArrayList<Integer> getPossibleMoves(Board b, int playerNum) {
        ArrayList<Integer> possibleMoves = new ArrayList<Integer>();
        for (int i = 1; i <= NUMBER_OF_HOUSES / 2; i++) {
            try {
                possibleMoves.add(checkMove(b.clone(), i, playerNum));
            }
            catch (Exception f) {}
        }
        return possibleMoves;
    }
    
    public void move(Board b, int move, int playerNum) {
        try {
            b.makeMove(move, playerNum);
        } catch (Exception e) { GameManagerImpl.out.println(e.getMessage()); }
    }
    
    public String toString() {
        return "1 " + name;
    }
    
    /**
     * returns true is this is a computer player. Computer players are limited to one second per move (on E216 computers) and forfeit the game if they quit or make an invalid move.
     **/
    public boolean isComputer() {
        return true;
    }
    
    /**
     * set the input stream for human commands (house numbers and QUIT).
     *
     **/
    public void setIn(InputStream in) {
        this.in = in;
    }
    
    /**
     * set the output stream for board state
     */
    public void setOut(PrintStream out) {
        this.out = out;
    }
}
