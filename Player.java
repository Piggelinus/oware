import java.io.InputStream;
import java.io.PrintStream;

/**
 * Player objects represent players in the game: they select their move based on the Board they are given and which side of the board they are playing. They may quit the game before a result is decided. Results are decided by the {@link Game}.
 *
 * You need to provide two implementations of this interface, one called HumanPlayer, which acts under control of the user, and one called ComputerPlayer which plays automatically and as well as it can within the time constraints.
 *
 * Input from the human players and output to the players can be redirected with in and out. Your Player should always use in and out for input and output rather than System.in and System.out.
 * 
 * A default contructor with no parameters is required for each imeplementation of the interface. By default the input should be set to System.in and the output stream to System.out
 *
 * @author Steven Bradley
 * @version 1.0
 */

public interface Player
{
    
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
     * @throws QuitGameException if, instead of choosing a house, a human player chooses to quit by entering 'QUIT'. If a computer player throws QuitGameException they forfeit the game ({@link #isComputer()}).
     *
     **/
    int getMove(Board b, int playerNum) throws QuitGameException;
    
    /**
     * returns true is this is a computer player. Computer players are limited to one second per move (on E216 computers) and forfeit the game if they quit or make an invalid move.
     **/
    boolean isComputer();

    
    /**
     * set the input stream for human commands (house numbers and QUIT).
     *
     **/
    void setIn(InputStream in);
    
    /**
     * set the output stream for board state 
     */
    void setOut(PrintStream out);
}
