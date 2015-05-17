import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

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

public class HumanPlayer implements Player {
    
    private InputStream in = System.in;
    private PrintStream out = System.out;
    private Scanner scanner;
    
    private String name;
    
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
    
     /*
    public static void main(String[] args) {
        HumanPlayer player = new HumanPlayer();
        int playerNum = 1;
        Board b = new BoardImpl();
        try {
            int house = player.getMove(b.clone(), playerNum);
            b.makeMove(house, playerNum);
            player.out.println(b);
        }
        catch (Exception e) {
            player.out.println(e.getMessage());
        }
    }
    */
    
    public HumanPlayer() {
        scanner = new Scanner(in);
        this.name = "human";
    }
    
    public HumanPlayer(String name) {
        scanner = new Scanner(in);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    public int getMove(Board b, int playerNum) throws QuitGameException {
        out.println(b);
        while (true) {
            String token = scanner.next();
            if (token.toLowerCase().equals("quit")) {
                throw new QuitGameException("Game quit");
            }
            else {
                try {
                    int house = new Integer(token);
                    return checkMove(b.clone(), house, playerNum);
                }
                catch (InvalidHouseException e) {
                    out.println(e.getMessage());
                }
                catch (InvalidMoveException e) {
                    out.println(e.getMessage());
                }
                catch (Exception e) {
                    out.println("Invalid input: please input a number in the range 1..6");
                }
            }
        }
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
    
    public String toString() {
        return "0 " + name;
    }
    
    /**
     * returns true is this is a computer player. Computer players are limited to one second per move (on E216 computers) and forfeit the game if they quit or make an invalid move.
     **/
    public boolean isComputer() {
        return false;
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
