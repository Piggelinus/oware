/**
 * Game objects represent the state of the game, including the board, the players, the current player
 * and the history of board states.
 *
 * Provide one constructor that makes a new game with the two players of the game as parameters.
 * Initialise the board state to have four seeds in each house.
 *
 * @author Steven Bradley
 * @version 1.1
 */

public interface Game
    {
        
        Player getCurrentPlayer();
        
        int getCurrentPlayerNum();
        
        Board getCurrentBoard();
        
        
        /**
         @return 1 or 2 corresponding to the winning player, if the game is over and won. Return 0 if the game is over and it is a draw. Return a negative value if the game is not over.
         **/
        
        int getResult();
        
        /**
         * @return true if the current board position has occurred before in the history of the game.
         **/
        
        boolean positionRepeated();
        
        /**
         * Get a move from the current player and update the board and current player
         */
        void nextMove() throws InvalidHouseException, InvalidMoveException, QuitGameException;
        
        /**
         * override the toString method to provide a summary of the game state (including the board)
         **/
        String toString();
    }