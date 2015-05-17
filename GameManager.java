import java.util.Comparator;
import java.io.InputStream;
import java.io.PrintStream;


/**
 *
 * GameManager objects allow the user to start, load, save and play game(s). In the main method, interaction with the game user is via the console.
 *
 * You need to provide a main method where the first command line parameter can optionally specify a saved game to load ({@link #loadGame loadGame()}) and play ({@link #playGame playGame()}).
 *
 * If a game is not underway the user may start a new game by entering 'NEW _player1type_ _player2type_' where the player types are either 'Human' or 'Computer'. The user may optionally specify player names with a colon but no space. E.g. 'NEW Human:Steven Computer:Orac' will start a new game between a human player called 'Steven' and a computer player called 'Orac'.
 *
 * If a game is not underway the user may load a previously saved game by entering 'LOAD _fname_' where _fname_ is the name of a previously saved game file ({@link #loadGame loadGame()}). The game is then restarted ({@link #playGame playGame()}).
 *
 * If a game is not underway but a game has been set up through LOAD or NEW or command line parameter then the game can be saved with 'SAVE _fname_'.
 *
 * If a game is not underway (e.g. because the game has finished) then the command 'EXIT' halts the program.
 
 * The GameManager also implements Comparator on Player by playing two games, with the two players taking turns to start. If the two games yield symmetric results (e.g. player 1 wins, player 2 wins) the the players are considered equal, otherwise the better player is better ...
 *
 * You need to provide a default constructor (no parameters) for a GameManager.
 *
 * @author Steven Bradley
 * @version 1.0
 *
 */

public interface GameManager extends Comparator<Player>{
    
    /** @return The current game object **/
    Game getGame();
    
    /**
     * Load the game state from the given file.
     *
     * @param fname The name of the file to load.
     *
     * @throws FileFailedException If, for whatever reason, the game file could not be loaded.
     *
     */
    
    void loadGame(String fname) throws FileFailedException;
    
    /**
     * Save the game state to the given file.
     *
     * @param fname The name of the file to save.
     *
     * @throws FileFailedException If, for whatever reason, the game file could not be saved (including file already exists).
     *
     */
    
    void saveGame(String fname) throws FileFailedException;

    /**
     * Play the current game to completion, returning the playerNum of the winning player (1..2) or 0 if the game ends in a draw. Input is taken from in ({@link #manage manage()}). If a computer player quits the game then they lose. If a computer player takes longer than one second for any move then it loses. After each turn is taken the game state (toString()) is sent to out.
     
     * @throws QuitGameException If a human player quits the game via QuitGameException.
     **/
    
    int playGame() throws QuitGameException;
    
    /**
     * accept input commands, including LOAD, SAVE, NEW, EXIT from the specified InputStream. All output is sent to the specified PrintStream. Can be used for testing the gameManager class via predefined inputs (e.g. a file) and writing the output to file. The specified input stream and output streams are also used for any player move input and output (i.e. selecting moves and QUIT). If a result is achieved this should be announced to the user.
     *
     * @param in The InputStream to be used for setting up and playing the game: System.in when not testing.
     *
     * @param out The OutputStream to be used for sending messages to the user while setting up and playing the game: System.out when not testing.
     *
     * @return The Game state after the instructions have been followed.
     **/
    Game manage(InputStream in, PrintStream out);
    
}
