import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.*;

public class GameTest {

    private Game game;
    private Player playerOne;
    private Player playerTwo;

    public GameTest() {

    }

    @Before
    public void setUp() {
        playerOne = new ComputerPlayer();
        playerTwo = new ComputerPlayer();
        game = new GameImpl(playerOne, playerTwo);
    }

    @After
    public void tearDown() {

    }

    /*
     * getCurrentPlayer()
     */

    @Test
    public void testGetCurrentPlayer() {
        assertEquals(game.getCurrentPlayer(), playerOne);
        try {
            game.nextMove();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(game.getCurrentPlayer(), playerTwo);
    }

    /*
     * getCurrentPlayerNum()
     */

    @Test
    public void testGetCurrentPlayerNum() {
        assertEquals(game.getCurrentPlayerNum(), 1);
        try {
            game.nextMove();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(game.getCurrentPlayerNum(), 2);
    }

    /*
     * getCurrentBoard()
     */

    @Test
    public void testGetCurrentBoard() {
        assertEquals(game.getCurrentBoard(), new BoardImpl());
    }

    /*
     * getResult()
     */

    @Test
    public void testGetResult() {
        int gamesToPlay = 100;
        for (int i = 0; i < gamesToPlay; i++) {
            game = new GameImpl(new RandomPlayer(), new RandomPlayer());
            assertEquals(game.getResult(), -1);
            try {
                ((GameImpl)game).play();
            } catch (QuitGameException e) {
                fail(e.getMessage());
            }
            int endResult = game.getResult();
            assertTrue(endResult >= 0 && endResult <= 2);
        }
    }
}