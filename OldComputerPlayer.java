import java.io.InputStream;
import java.io.PrintStream;

import java.util.*;
import java.io.File;
import java.io.FileWriter;

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

public class ComputerPlayer implements Player {
    
    private InputStream in = System.in;
    private PrintStream out = System.out;
    
    private String name;
    private int myPlayerNum;
    private HashMap<String, Integer> base;
    private int LOOKAHEAD_DEPTH = 3;
    
    private boolean usingKnowledgeBase = false;
    
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
        ComputerPlayer AI = new ComputerPlayer("AI");
        Board board = new BoardImpl();
        AI.out.println(board);
        try {
            board.makeMove(AI.getMove(board, 1), 1);
            AI.out.println(board);
        }
        catch (Exception e) {
            AI.out.println("Something went wrong");
        }
    }
    
    public ComputerPlayer() {
        this.name = "computer";
        base = new HashMap<String, Integer>();
        if (usingKnowledgeBase) {
            try {
                loadKnowledgeBase(KnowledgeBase.FILE_NAME);
            } catch (Exception e) {}
        }
    }
    
    public ComputerPlayer(String name) {
        this.name = name;
        base = new HashMap<String, Integer>();
        if (usingKnowledgeBase) {
            try {
                loadKnowledgeBase(KnowledgeBase.FILE_NAME);
            } catch (Exception e) {}
        }
    }
    
    public ComputerPlayer(int difficulty) {
        this.name = "computer";
        LOOKAHEAD_DEPTH = difficulty;
        base = new HashMap<String, Integer>();
        if (usingKnowledgeBase) {
            try {
                loadKnowledgeBase(KnowledgeBase.FILE_NAME);
            } catch (Exception e) {}
        }
    }
    
    public ComputerPlayer(String name, int difficulty) {
        this.name = name;
        LOOKAHEAD_DEPTH = difficulty;
        base = new HashMap<String, Integer>();
        if (usingKnowledgeBase) {
            try {
                loadKnowledgeBase(KnowledgeBase.FILE_NAME);
            } catch (Exception e) {}
        }
    }
    
    public String getName() {
        return name;
    }
    
    public int getMove(Board b, int playerNum) throws QuitGameException {
        out.println(b);
        myPlayerNum = playerNum;
        if (usingKnowledgeBase) {
            int knowledgedMove = knowledgedMove(b, playerNum);
            if (knowledgedMove > 0) {
                return knowledgedMove;
            }
        }
        return chooseMove(b.clone(), playerNum);
    }
    
    public int knowledgedMove(Board b, int playerNum) {
        List<Integer> results = new ArrayList<Integer>();
        int moveResultingInTie = 0;
        if (base.size() > 0) {
            for (int move : getPossibleMoves(b, playerNum)) {
                Board b_clone = b.clone();
                try {
                    b_clone.makeMove(move, playerNum);
                } catch (Exception e) {}
                String boardState = ((playerNum % 2) + 1) + getBoardState(b_clone);
                out.println(boardState + " " + base.get(boardState));
                if (base.containsKey(boardState)) {
                    int thisStateValue = base.get(boardState);
                    if (thisStateValue == playerNum) {
                        out.println("winning move");
                        return move;
                    }
                    else if (thisStateValue == 3) {
                        out.println("worst case draw");
                        moveResultingInTie = move;
                    }
                }
            }
        }
        if (moveResultingInTie > 0) {
            return moveResultingInTie;
        }
        return 0;
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
    
    /**
     * Räkna hur många 1:or och 2:or som finns på motståndarens sida (ger pluspoäng)
     * 1:or och 2:or på egen sida ger minuspoäng
     *
     * Lägg till och med in extrapoäng för 1:or och 2:or på rad.
     * 2:or ger mer poäng
     **/
    
    public int minmax(Board b, int playerNum, int i) {
        ArrayList<Integer> moveScores = new ArrayList<Integer>();
        ArrayList<Integer> possibleMoves = getPossibleMoves(b.clone(), playerNum);
        int bestOpponentScore = 999;
        Board nextStateAfterOpponent = null;
        if (possibleMoves.size() > 1) {
            for (int move : possibleMoves) {
                Board b_clone = b.clone();
                int netScore = heuristicValue(b_clone, playerNum, move, i);
                if (netScore < -5000 || netScore > 5000) {
                    return netScore;
                }
                if (i < LOOKAHEAD_DEPTH - 1 && playerNum == myPlayerNum) {
                    netScore += minmax(b_clone.clone(), (playerNum % 2) + 1, i + 1);
                }
                else if (playerNum != myPlayerNum) {
                    if (nextStateAfterOpponent == null || netScore < bestOpponentScore) {
                        bestOpponentScore = netScore;
                        nextStateAfterOpponent = b_clone;
                    }
                }
                moveScores.add(netScore);
            }
            if (playerNum == myPlayerNum) {
                return moveScores.indexOf(max(moveScores));
            }
            else {
                return bestOpponentScore + minmax(nextStateAfterOpponent.clone(), (playerNum % 2) + 1, i + 1);
            }
        }
        else if (possibleMoves.size() == 1) {
            return heuristicValue(b.clone(), playerNum, possibleMoves.get(0), i);
        }
        else {
            return 0;
        }
    }
    
    public int chooseMove(Board b, int playerNum) {
        ArrayList<Integer> moveScores = new ArrayList<Integer>();
        HashMap<Integer, Integer> possibleMoves = new HashMap<Integer, Integer>();
        for (int move : getPossibleMoves(b, playerNum)) {
            Board b_clone = b.clone();
            possibleMoves.put(move, heuristicValue(b_clone, playerNum, move, 0) + minmax(b_clone.clone(), (playerNum % 2) + 1, 1));
        }
        if (playerNum == myPlayerNum) {
            int max = max(possibleMoves);
            //out.println("best move " + max);
            return max;
        }
        else {
            return min(possibleMoves);
        }
    }
    
    public int min(List<Integer> list) {
        return Collections.min(list);
    }
    
    public int max(List<Integer> list) {
        return Collections.max(list);
    }
    
    public int max(Map<Integer, Integer> map) {
        Map.Entry<Integer, Integer> maxEntry = null;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
    }
    
    public int min(Map<Integer, Integer> map) {
        Map.Entry<Integer, Integer> maxEntry = null;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) < 0) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
    }
    
    public int heuristicValue(Board b, int playerNum, int move, int depth) {
        int myScoreBefore = b.getScore(playerNum);
        int yourScoreBefore = b.getScore((playerNum % 2) + 1);
        move(b, move, playerNum);
        int myScoreAfter = b.getScore(playerNum);
        int yourScoreAfter = b.getScore((playerNum % 2) + 1);
        int winner = 0;
        for (int i = 1; i <= NUMBER_OF_PLAYERS; i++) {
            if (b.getScore(i) > SEEDS_IN_HOUSES * (NUMBER_OF_HOUSES / 2)) {
                winner = i;
            }
        }
        if (winner > 0) {
            if (winner == myPlayerNum) {
                return 10000;
            }
            else {
                return -10000;
            }
        }
        int netScore;
        if (playerNum == myPlayerNum) {
            netScore = (myScoreAfter - myScoreBefore) - (yourScoreAfter - yourScoreBefore);
        }
        else {
            netScore = (yourScoreAfter - yourScoreBefore) - (myScoreAfter - myScoreBefore);
        }
        netScore += (2 / (depth + 1));
        return netScore;
    }
    
    public void move(Board b, int move, int playerNum) {
        try {
            b.makeMove(move, playerNum);
        } catch (Exception e) { out.println(e.getMessage()); }
    }
    
    public void loadKnowledgeBase(String fname) throws FileFailedException {
        try {
            File file = new File(fname);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                Scanner lineReader = new Scanner(reader.nextLine());
                String boardState = lineReader.next();
                int value = new Integer(lineReader.next());
                /*
                 String stateWithSpaces = "";
                 String space = " ";
                 for (int i = 0; i < boardState.length(); i++) {
                 stateWithSpaces += boardState.substring(i, i + 1);
                 if (i < boardState.length() - 1) {
                 stateWithSpaces += space;
                 }
                 }
                 Scanner stateReader = new Scanner(stateWithSpaces);
                 int[] scoreHouses = new int[2];
                 int[] houses = new int[NUMBER_OF_HOUSES];
                 scoreHouses[0] = new Integer(stateReader.next());
                 scoreHouses[1] = new Integer(stateReader.next());
                 for (int i = 0; i < houses.length; i++) {
                 houses[i] = new Integer(stateReader.next());
                 }
                 BoardImpl b = new BoardImpl(houses, scoreHouses);
                 */
                base.put(boardState, value);
            }
        }
        catch (Exception e) {
            out.println(e.getMessage());
        }
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
            return (playerNum % 2) + 1;
        } else {
            return playerNum;
        }
    }
    
    public String getBoardState(Board board) {
        String representation = "";
        String space = " ";
        representation += board.getScore(1) + space + board.getScore(2) + space;
        for (int i = 1; i <= NUMBER_OF_HOUSES; i++) {
            try {
                representation += board.getSeeds(houseParam(i), playerNumParam(1, i)) + space;
            } catch (Exception e) {}
        }
        return representation.replaceAll(" ", "");
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
