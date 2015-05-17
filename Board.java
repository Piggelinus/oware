/**
 *
 * Board objects represent the state of the Oware board, including the number of seeds in each of the houses
 * and in the two score houses, where captured seeds are placed. Players are numbered 1 and 2 and each player has houses numbered 1..6.
 *
 * Board objects are updatable by accepting moves.
 *
 * Board objects are constructed either from nothing (default constructor), or by cloning. Cloning is needed during game play to ensure that players are not able to update the game board directly.
 *
 * See <a href='//en.wikipedia.org/wiki/Oware'>en.wikipedia.org/wiki/Oware</a> for the layout and rules of Oware. These are summarised in <a href='//community.dur.ac.uk/s.p.bradley/teaching/IP/assignment_oware/'>community.dur.ac.uk/s.p.bradley/teaching/IP/assignment_oware/</a>/
 *
 * @author Steven Bradley
 * @version 1.0
 *
 */

public interface Board
    {
        
        /**
         * update the board to make a move from the specified house of the specified player
         *
         * @param house in range 1..6 representing the house position (starting from anticlockwise)
         *
         * @param playerNum in range 1..2
         *
         * @throws InvalidHouseException if the range or playerNum are not in the right range
         *
         * @throws InvalidMoveException if  the house does not
         * represent a valid move because either the house is empty, or the move would leave the opponent without
         * a move to make
         *
         **/
        
        void makeMove(int house, int playerNum) throws InvalidHouseException, InvalidMoveException;
        
        
        /**
         * the number of seeds in the specified house of the specified player. See {@link Board#makeMove makeMove()} for parameterDetails
         *
         */
        int getSeeds(int house, int playerNum) throws InvalidHouseException;
        
        /**
         * sow a seed in a location: increase the number of seeds already there by one
         **/
        void sowSeed(int house, int playerNum) throws InvalidHouseException;
        
        /**
         * set the number of seeds in a house to a given value
         */
        void setSeeds(int seeds, int house, int playerNum) throws InvalidHouseException;
        
        /**
         * find the number of seeds in a player score house
         **/
        int getScore(int playerNum);
        
        
        /**
         * increase a player's score by putting seeds into their score house
         **/
        void addScore(int seeds, int playerNum);
        
        /**
         * set the number of seeds in a player's score house
         */
        void setScore(int seeds, int playerNum);
        
        
        /**
         * override the toString method to provide a summary of the board state
         **/
        String toString();
        
        /**
         * override the clone method to copy a board state that can be passed to a player;
         **/
        Board clone();
        
        /**
         * override equals
         *
         **/
        
        boolean equals(Object o);
        
    }
