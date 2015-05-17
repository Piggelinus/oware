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

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BoardImpl implements Board {

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

    final static int NUMBER_OF_PLAYERS = 2;
    public static int NUMBER_OF_HOUSES = 12; //Deafult 12;
    public static int SEEDS_IN_HOUSES = 4; //Default 4;

    final static String ANSI_CLS = "\u001b[2J";
    final static String ANSI_HOME = "\u001b[H"; //Clears the screen in OSX Terminal

    private int[] houses;
    private int[] scoreHouses;

    public static void main(String[] args) {
        BoardImpl board = new BoardImpl();
        boolean playing = true;
        board.clearBoard();
        GameManagerImpl.out.println(board);
        for (int playerNum = 1; playing; playerNum = (playerNum % 2) + 1) {
            Scanner sc = new Scanner(GameManagerImpl.in);
            try {
                board.makeMove(new Integer(sc.next()), playerNum);
                board.clearBoard();
                GameManagerImpl.out.println(board);
            }
            catch (Exception e) {
                GameManagerImpl.out.println(e.getMessage());
            }
        }
    }

    public BoardImpl() {
        houses = new int[NUMBER_OF_HOUSES];
        for (int i = 0; i < NUMBER_OF_HOUSES; i++) {
            houses[i] = SEEDS_IN_HOUSES;
        }
        scoreHouses = new int[NUMBER_OF_PLAYERS];
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            scoreHouses[i] = 0;
        }
    }

    public BoardImpl(int startingSeeds) {
        houses = new int[NUMBER_OF_HOUSES];
        for (int i = 0; i < NUMBER_OF_HOUSES; i++) {
            SEEDS_IN_HOUSES = startingSeeds;
            houses[i] = SEEDS_IN_HOUSES;
        }
        scoreHouses = new int[NUMBER_OF_PLAYERS];
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            scoreHouses[i] = 0;
        }
    }

    public BoardImpl(int startingSeeds, int numberOfHouses) {
        SEEDS_IN_HOUSES = startingSeeds;
        NUMBER_OF_HOUSES = numberOfHouses;
        houses = new int[NUMBER_OF_HOUSES];
        for (int i = 0; i < NUMBER_OF_HOUSES; i++) {
            houses[i] = SEEDS_IN_HOUSES;
        }
        scoreHouses = new int[NUMBER_OF_PLAYERS];
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            scoreHouses[i] = 0;
        }
    }

    public BoardImpl(int[] houses, int[] scoreHouses) {
        this.houses = houses.clone();
        this.scoreHouses = scoreHouses.clone();
    }
    
    public int houseParam(int house) {
        if (house > NUMBER_OF_HOUSES / 2) {
            return house - (NUMBER_OF_HOUSES / 2);
        } else {
            return house;
        }
    }
    
    public int playerNumParam(int playerNum, int house) {
        if (house > NUMBER_OF_HOUSES / 2) {
            return GameImpl.otherPlayerNum(playerNum);
        } else {
            return playerNum;
        }
    }

    public void makeMove(int house, int playerNum) throws InvalidHouseException, InvalidMoveException {
        if (house >= 1 && house <= NUMBER_OF_HOUSES / 2 && playerNum >= 1 && playerNum <= NUMBER_OF_PLAYERS) {
            int seeds = getSeeds(house, playerNum);
            if (seeds > 0) {
                setSeeds(0, house, playerNum);
                List<Integer> housesToReap = new ArrayList<Integer>();
                int seedsSown;
                int houseToSow = 0;
                for (seedsSown = 1; seedsSown <= seeds; ++seedsSown) {
                    houseToSow = ((house + seedsSown - 1) % NUMBER_OF_HOUSES) + 1;
                    if (houseToSow != house) {
                        sowSeed(houseParam(houseToSow), playerNumParam(playerNum, houseToSow));
                    }
                    else {
                        seeds++;
                    }
                }
                capture(housesToReap, houseToSow, playerNum);
                BoardImpl b = clone();
                b.reap(housesToReap, playerNum);
                if (!b.outOfSeeds(GameImpl.otherPlayerNum(playerNum))) {
                    reap(housesToReap, playerNum);
                }
            }
            else {
                throw new InvalidMoveException("Invalid input: house is empty");
            }
        }
        else {
            throw new InvalidHouseException("Invalid input: house not within range (1..6)");
        }
    }

    public void capture(List<Integer> housesToReap, int house, int playerNum) throws InvalidHouseException {
        int seedsInHouse = getSeeds(houseParam(house), playerNumParam(playerNum, house));
        if (((house - 1) % NUMBER_OF_HOUSES) + 1 >= (NUMBER_OF_HOUSES / 2) + 1 && ((house - 1) % NUMBER_OF_HOUSES) + 1 <= NUMBER_OF_HOUSES) {
            if (seedsInHouse == 2 || seedsInHouse == 3) {
                housesToReap.add(house);
                capture(housesToReap, (house - 1) % NUMBER_OF_HOUSES, playerNum);
            }
        }
    }

    public void reap(List<Integer> housesToReap, int playerNum) throws InvalidHouseException {
        for (int house : housesToReap) {
            int seedsInHouse = getSeeds(houseParam(house), playerNumParam(playerNum, house));
            setSeeds(0, houseParam(house), playerNumParam(playerNum, house));
            addScore(seedsInHouse, playerNum);
        }
    }

    public void reapAll() {
        ArrayList<Integer> rowOfHouses = new ArrayList<Integer>();
        for (int i = 1; i <= NUMBER_OF_HOUSES / 2; i++) {
            rowOfHouses.add(i);
        }
        try {
            reap(rowOfHouses, 1);
            reap(rowOfHouses, 2);
        } catch (Exception e) {}
    }

    public void reapAll(int playerNum) {
        ArrayList<Integer> rowOfHouses = new ArrayList<Integer>();
        for (int i = 1; i <= NUMBER_OF_HOUSES; i++) {
            rowOfHouses.add(i);
        }
        try {
            reap(rowOfHouses, playerNum);
        } catch (Exception e) {}
    }

    public boolean outOfSeeds(int playerNum) throws InvalidHouseException {
        int seeds = 0;
        for (int house = 1; house <= NUMBER_OF_HOUSES / 2; house++) {
            seeds += getSeeds(house, playerNum);
        }
        if (seeds > 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * the number of seeds in the specified house of the specified player. See {@link Board#makeMove makeMove()} for parameterDetails
     *
     */
    public int getSeeds(int house, int playerNum) throws InvalidHouseException {
        if (house >= 1 && house <= NUMBER_OF_HOUSES / 2) {
            if (playerNum >= 1 && playerNum <= NUMBER_OF_PLAYERS) {
                int currentHouse = ((house - 1) + ((NUMBER_OF_HOUSES / 2) * (playerNum - 1))) % NUMBER_OF_HOUSES;
                return houses[currentHouse];
            }
            else {
                throw new IllegalArgumentException("Invalid input: player number not allowed");
            }
        }
        else {
            throw new InvalidHouseException("Invalid input: house not within range (1..6) in getSeeds()");
        }
    }

    /**
     * sow a seed in a location: increase the number of seeds already there by one
     **/
    public void sowSeed(int house, int playerNum) throws InvalidHouseException {
        setSeeds(getSeeds(house, playerNum) + 1, house, playerNum);
    }

    /**
     * set the number of seeds in a house to a given value
     */
    public void setSeeds(int seeds, int house, int playerNum) throws InvalidHouseException {
        if (house >= 1 && house <= NUMBER_OF_HOUSES / 2) {
            if (playerNum >= 1 && playerNum <= NUMBER_OF_PLAYERS) {
                int currentHouse = ((house - 1) + ((NUMBER_OF_HOUSES / 2) * (playerNum - 1))) % NUMBER_OF_HOUSES;
                houses[currentHouse] = seeds;
            }
            else {
                throw new IllegalArgumentException("Invalid input: player number not allowed");
            }
        }
        else {
            throw new InvalidHouseException("Invalid input: house not within range (1..6) in setSeeds()");
        }
    }

    /**
     * find the number of seeds in a player score house
     **/
    public int getScore(int playerNum) {
        if (playerNum >= 1 && playerNum <= NUMBER_OF_PLAYERS) {
            return scoreHouses[playerNum - 1];
        }
        else {
            throw new IllegalArgumentException("Invalid input: player number not allowed");
        }
    }

    /**
     * increase a player's score by putting seeds into their score house
     **/
    public void addScore(int seeds, int playerNum) {
        setScore(getScore(playerNum) + seeds, playerNum);
    }

    /**
     * set the number of seeds in a player's score house
     */
    public void setScore(int seeds, int playerNum) {
        if (playerNum >= 1 && playerNum <= NUMBER_OF_PLAYERS && seeds >= 0 && seeds <= NUMBER_OF_HOUSES * SEEDS_IN_HOUSES) {
            scoreHouses[playerNum - 1] = seeds;
        }
        else {
            throw new IllegalArgumentException("Invalid input: player number not allowed or number of seeds not allowed");
        }
    }

    public String getBoardState() {
        String representation = "";
        String space = " ";
        representation += getScore(1) + space + getScore(2) + space;
        for (int i = 0; i < NUMBER_OF_HOUSES; i++) {
            representation += houses[i] + space;
        }
        return representation;
    }

    public void clearBoard() {
        GameManagerImpl.out.print(ANSI_CLS + ANSI_HOME);
        GameManagerImpl.out.flush();
    }

    /**
     * override the toString method to provide a summary of the board state
     **/
    public String toString() {
        String space = " ";
        String newLine = "\n";
        String representation = space + space;
        for (int i = NUMBER_OF_HOUSES - 1; i >= NUMBER_OF_HOUSES / 2; i--) {
            representation += houses[i] + space;
        }
        representation += newLine + getScore(2);
        for (int j = 0; j < NUMBER_OF_HOUSES; j++) {
            representation += space;
        }
        representation += space + getScore(1) + newLine + space + space;
        for (int i = 0; i <= (NUMBER_OF_HOUSES / 2) - 1; i++) {
            representation += houses[i] + space;
        }
        return representation;
    }

    /**
     * override the clone method to copy a board state that can be passed to a player;
     **/
    public BoardImpl clone() {
        return new BoardImpl(houses, scoreHouses);
    }

    /**
     * override equals
     *
     **/

    @Override
    public boolean equals(Object o) {
        if (this.toString().equals(((BoardImpl)o).toString())) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
