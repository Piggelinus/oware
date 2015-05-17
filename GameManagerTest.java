import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.*;

public class GameManagerTest {
    
    private GameManagerImpl gm;
    
    public GameManagerTest() {
        
    }

    @Before
    public void setUp() {
        gm = new GameManagerImpl();
    }
    
    @After
    public void tearDown() {
        
    }
    
    /*
     * test for starvation
     */
    
    @Test
    public void testForStarvation() {
        try {
            gm.loadGameWithoutPlaying("starvation.txt");
            assertTrue(gm.playGame() == 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    /*
     * test for position repeated
     */
    
    @Test
    public void testForPositionRepeated() {
        try {
            gm.loadGameWithoutPlaying("positionrepeated.txt");
            assertTrue(gm.playGame() == 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    /*
     * test for no score change in 100 moves
     */
    
    @Test
    public void testForNoScoreChange() {
        try {
            gm.loadGameWithoutPlaying("noscorechange.txt");
            assertTrue(gm.playGame() == 2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    /*
     * test for timed out move
     */
    
    @Test
    public void testForMoveTimedOut() {
        GameManager gm2 = new GameManagerImpl(new ComputerPlayer("a", 12), new ComputerPlayer("b"));
        try {
            assertTrue(gm2.playGame() == 2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    /*
     * test for grand slam
     */
    
    @Test
    public void testForGrandSlam() {
        try {
            gm.loadGameWithoutPlaying("grandslam.txt");
            assertTrue(gm.playGame() == 1);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    /*
     * test for validate board
     */
    
    @Test
    public void testForValidateBoard() {
        try {
            gm.loadGameWithoutPlaying("validgame.txt");
        } catch (Exception e) {
            fail(e.getMessage());
        }
        try {
            gm.loadGameWithoutPlaying("invalidgame.txt");
        } catch (FileFailedException e) {
            
        }
    }
    
    /*
     * test for save and load
     */
    
    @Test
    public void testForSaveAndLoad() {
        try {
            gm.newGameWithoutPlaying(new ComputerPlayer(), new ComputerPlayer());
            gm.getGame().nextMove();
            Game gameBeforeSave = gm.getGame();
            gm.saveGame("saveandload.txt");
            gm.loadGameWithoutPlaying("saveandload.txt");
            Game gameAfterLoad = gm.getGame();
            assertEquals(gameBeforeSave, gameAfterLoad);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
