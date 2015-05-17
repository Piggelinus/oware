import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.*;

public class BoardTest {

    private Board board;

    public BoardTest() {

    }

    @Before
    public void setUp() {
        board = new BoardImpl();
    }

    @After
    public void tearDown() {

    }

    /*
     * clone()
     */

    @Test
    public void testCloneShouldWork() {
        try {
            Board b = board.clone();
            b.setSeeds(0, 1, 1);
            for (int i = 1; i <= 6; i++) {
                try {
                    assertTrue(board.getSeeds(i, 1) == 4);
                    assertTrue(board.getSeeds(i, 2) == 4);
                } catch (Exception e) {
                    fail(e.getMessage());
                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /*
     * getSeeds()
     */

    @Test
    public void testGetSeedsShouldWork() {
        for (int i = 1; i <= 6; i++) {
            try {
                assertTrue(board.getSeeds(i, 1) == 4);
                assertTrue(board.getSeeds(i, 2) == 4);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void testGetSeedsShouldThrowIllegalArgumentException() {
        try {
            assertTrue(board.getSeeds(1, 3) == 4);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            try {
                assertTrue(board.getSeeds(1, 0) == 4);
            } catch (IllegalArgumentException f) {

            } catch (InvalidHouseException f) {
                fail("expected IllegalArgumentException");
            }
        } catch (InvalidHouseException e) {
            fail("expected IllegalArgumentException");
        }}

    @Test
    public void testGetSeedsShouldThrowInvalidHouseException() {
        try {
            assertTrue(board.getSeeds(0, 1) == 4);
            fail("expected InvalidHouseException");
        } catch (InvalidHouseException e) {
            try {
                assertTrue(board.getSeeds(7, 1) == 4);
            } catch (InvalidHouseException f) {

            } catch (IllegalArgumentException f) {
                fail("expected InvalidHouseException");
            }
        } catch (IllegalArgumentException e) {
            fail("expected InvalidHouseException");
        }
    }

    /*
     * setSeeds()
     */

    @Test
    public void testSetSeedsShouldWork() {
        for (int i = 1; i <= 6; i++) {
            try {
                Board a = board.clone();
                Board b = board.clone();
                a.setSeeds(1, i, 1);
                b.setSeeds(1, i, 2);
                assertTrue(a.getSeeds(i, 1) == 1);
                assertTrue(b.getSeeds(i, 2) == 1);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void testSetSeedsShouldThrowIllegalArgumentException() {
        try {
            board.clone().setSeeds(1, 1, 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            try {
                board.clone().setSeeds(1, 1, 3);
                fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException f) {

            } catch (InvalidHouseException f) {
                fail("expected IllegalArgumentException");
            }
        } catch (InvalidHouseException e) {
            fail("expected IllegalArgumentException");
        }
    }

    @Test
    public void testSetSeedsShouldThrowInvalidHouseException() {
        try {
            board.clone().setSeeds(1, 0, 1);
            fail("expected InvalidHouseException");
        } catch (InvalidHouseException e) {
            try {
                board.clone().setSeeds(1, 7, 1);
                fail("expected InvalidHouseException");
            } catch (InvalidHouseException f) {
                try {
                    board.clone().setSeeds(1, 0, 2);
                    fail("expected InvalidHouseException");
                } catch (InvalidHouseException g) {
                    try {
                        board.clone().setSeeds(1, 7, 2);
                        fail("expected InvalidHouseException");
                    } catch (InvalidHouseException h) {

                    } catch (IllegalArgumentException h) {
                        fail("expected InvalidHouseException");
                    }
                } catch (IllegalArgumentException g) {
                    fail("expected InvalidHouseException");
                }
            } catch (IllegalArgumentException f) {
                fail("expected InvalidHouseException");
            }
        } catch (IllegalArgumentException e) {
            fail("expected InvalidHouseException");
        }
    }

    /*
     * sowSeed()
     */

    @Test
    public void testSowSeedShouldWork() {
        for (int i = 1; i <= 6; i++) {
            try {
                Board a = board.clone();
                Board b = board.clone();
                a.sowSeed(i, 1);
                b.sowSeed(i, 2);
                assertTrue(a.getSeeds(i, 1) == 5);
                assertTrue(b.getSeeds(i, 2) == 5);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void testSowSeedShouldThrowIllegalArgumentException() {
        try {
            board.clone().sowSeed(1, 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            try {
                board.clone().sowSeed(1, 3);
                fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException f) {
            } catch (InvalidHouseException f) {
                fail("expected IllegalArgumentException");
            }
        } catch (InvalidHouseException e) {
            fail("expected IllegalArgumentException");
        }
    }

    @Test
    public void testSowSeedShouldThrowInvalidHouseException() {
        try {
            board.clone().sowSeed(0, 1);
            fail("expected InvalidHouseException");
        } catch (InvalidHouseException e) {
            try {
                board.clone().sowSeed(7, 1);
                fail("expected InvalidHouseException");
            } catch (InvalidHouseException f) {
                try {
                    board.clone().sowSeed(0, 2);
                    fail("expected InvalidHouseException");
                } catch (InvalidHouseException g) {
                    try {
                        board.clone().sowSeed(7, 2);
                        fail("expected InvalidHouseException");
                    } catch (InvalidHouseException h) {

                    } catch (IllegalArgumentException h) {
                        fail("expected InvalidHouseException");
                    }
                } catch (IllegalArgumentException g) {
                    fail("expected InvalidHouseException");
                }
            } catch (IllegalArgumentException f) {
                fail("expected InvalidHouseException");
            }
        } catch (IllegalArgumentException e) {
            fail("expected InvalidHouseException");
        }
    }

    /*
     * getScore()
     */

    @Test
    public void testGetScoreShouldWork() {
        try {
            assertTrue(board.getScore(1) == 0);
            assertTrue(board.getScore(2) == 0);
        } catch (IllegalArgumentException e) {
            fail("did not expect IllegalArgumentException");
        }
    }

    @Test
    public void testGetScoreShouldThrowIllegalArgumentException() {
        try {
            assertTrue(board.getScore(0) == 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            try {
                assertTrue(board.getScore(3) == 0);
                fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException f) {

            }
        }
    }

    /*
     * setScore()
     */

    @Test
    public void testSetScoreShouldWork() {
        try {
            Board b = board.clone();
            b.setScore(1, 1);
            b.setScore(2, 2);
            assertTrue(b.getScore(1) == 1);
            assertTrue(b.getScore(2) == 2);
        } catch (IllegalArgumentException e) {
            fail("did not expect IllegalArgumentException");
        }
    }

    @Test
    public void testSetScoreShouldThrowIllegalArgumentException() {
        Board b = board.clone();
        try {
            b.setScore(1, 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            try {
                b.setScore(1, 3);
                fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException f) {
                try {
                    b.setScore(-1, 1);
                    fail("expected IllegalArgumentException");
                } catch (IllegalArgumentException g) {
                    try {
                        b.setScore(49, 2);
                        fail("expected IllegalArgumentException");
                    } catch (IllegalArgumentException h) {

                    }
                }
            }
        }
    }

    /*
     * addScore()
     */

    @Test
    public void testAddScoreShouldWork() {
        try {
            Board b = board.clone();
            assertTrue(b.getScore(1) == 0);
            b.addScore(1, 1);
            assertTrue(b.getScore(1) == 1);
            assertTrue(b.getScore(2) == 0);
            b.addScore(2, 2);
            assertTrue(b.getScore(2) == 2);
        } catch (IllegalArgumentException e) {
            fail("did not expect IllegalArgumentException");
        }
    }

    @Test
    public void testAddScoreShouldThrowIllegalArgumentException() {
        Board b = board.clone();
        try {
            b.addScore(1, 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            try {
                b.addScore(1, 3);
                fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException f) {
                try {
                    b.addScore(-1, 1);
                    fail("expected IllegalArgumentException");
                } catch (IllegalArgumentException g) {
                    try {
                        b.addScore(49, 2);
                        fail("expected IllegalArgumentException");
                    } catch (IllegalArgumentException h) {

                    }
                }
            }
        }
    }

    /*
     * makeMove()
     */

    @Test
    public void testMakeMoveShouldWork() {
        for (int i = 1; i <= 6; i++) {
            Board a = board.clone();
            Board b = board.clone();
            try {
                a.makeMove(i, 1);
                b.makeMove(i, 2);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void testMakeMoveShouldThrowInvalidHouseException() {
        Board b = board.clone();
        try {
            b.makeMove(0, 1);
            fail("expected InvalidHouseException");
        } catch (InvalidHouseException e) {
            try {
                b.makeMove(7, 1);
                fail("expected InvalidHouseException");
            } catch (InvalidHouseException f) {

            } catch (InvalidMoveException f) {
                fail("did not expect InvalidMoveException");
            }
        } catch (InvalidMoveException e) {
            fail("did not expect InvalidMoveException");
        }
    }

    @Test
    public void testMakeMoveShouldThrowInvalidMoveException() {
        try {
            Board a = board.clone();
            a.setSeeds(0, 1, 1);
            a.makeMove(1, 1);
            fail("expected InvalidMoveException");
        } catch (InvalidMoveException e) {
            try {
                Board b = board.clone();
                b.setSeeds(0, 6, 2);
                b.makeMove(6, 2);
                fail("expected InvalidMoveException");
            } catch (InvalidMoveException f) {

            } catch (InvalidHouseException f) {
                fail("did not expect InvalidHouseException");
            }
        } catch (InvalidHouseException e) {
            fail("did not expect InvalidHouseException");
        }
    }

    /*
     * equals()
     */

    @Test
    public void testEqualsShouldWork() {
        try {
            Board a = board.clone();
            assertEquals(board, a);
            a.setSeeds(0, 1, 1);
            assertThat(board, not(a));
            Board b = board.clone();
            b.setScore(1, 1);
            assertThat(board, not(b));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}