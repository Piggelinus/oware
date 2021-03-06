import java.io.InputStream;
import java.io.PrintStream;

import java.util.*;
import java.io.File;
import java.io.FileWriter;

/**
 * PLayer objects represent players in the game: they select their move based on the Board they are given and which side of the board they are playing. They may quit the game before a result is decided. Results are decided by the {@link Game}.
 *
 * You need to provide two implementations of this interface, one called HumanPlayer, which acts under control of the user, and one called Contender which plays automatically and as well as it can within the time constraints.
 *
 * Input from the human players and output to the players can be redirected with (@see in) and (@see out).
 *
 * A default contructor with no parameters is required for each imeplementation of the interface. By default the input should be set to System.in and the output stream to System.out
 *
 * @author Linus Ericsson
 * @version 0.1
 */

public class Contender implements Player {
    
    private InputStream in = System.in;
    private PrintStream out = System.out;
    
    private String name;
    private int myPlayerNum;
    private HashMap<String, Integer> base;
    private int LOOKAHEAD_DEPTH = 11;
    
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
        Contender AI = new Contender("AI");
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
    
    public Contender() {
        this.name = "computer";
        base = new HashMap<String, Integer>();
        if (usingKnowledgeBase) {
            try {
                loadKnowledgeBase(KnowledgeBase.FILE_NAME);
            } catch (Exception e) {}
        }
    }
    
    public Contender(String name) {
        this.name = name;
        base = new HashMap<String, Integer>();
        if (usingKnowledgeBase) {
            try {
                loadKnowledgeBase(KnowledgeBase.FILE_NAME);
            } catch (Exception e) {}
        }
    }
    
    public Contender(int difficulty) {
        this.name = "computer";
        LOOKAHEAD_DEPTH = difficulty;
        base = new HashMap<String, Integer>();
        if (usingKnowledgeBase) {
            try {
                loadKnowledgeBase(KnowledgeBase.FILE_NAME);
            } catch (Exception e) {}
        }
    }
    
    public Contender(String name, int difficulty) {
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
    
    public int pvs(Board initialBoard, Board b, int playerNum, int depth, int alpha, int beta) {
        if (depth == 0 || gameEnded(initialBoard, b, playerNum)) return evaluate(initialBoard, b, playerNum, depth);
        ArrayList<Integer> possibleMoves = getPossibleMoves(b, playerNum);
        for (int move : possibleMoves)  {
            Board b_clone = b.clone();
            move(b_clone, move, playerNum);
            int score;
            if (move != possibleMoves.get(0)) {
                score = -pvs(initialBoard, b_clone, (playerNum % 2) + 1, depth - 1, -alpha - 1, -alpha);
                if (alpha < score && score < beta) {
                    score = -pvs(initialBoard, b_clone, (playerNum % 2) + 1, depth - 1, -beta, -score);
                }
            }
            else {
                score = -pvs(initialBoard, b_clone, (playerNum % 2) + 1, depth - 1, -beta, -alpha);
            }
            if (score > alpha)
                alpha = score;
            if (alpha >= beta) {
                break;
            }
        }
        return alpha;
    }
    
    public int chooseMove(Board initialBoard, int playerNum) {
        int max = -1000000;
        int bestMove = 0;
        for (int move : getPossibleMoves(initialBoard, playerNum))  {
            Board b_clone = initialBoard.clone();
            move(b_clone, move, playerNum);
            int score = -pvs(initialBoard, b_clone, (playerNum % 2) + 1, LOOKAHEAD_DEPTH - 1, -1000000, 1000000);
            if (score > max) {
                max = score;
                bestMove = move;
            }
        }
        return bestMove;
    }
    
    public int evaluate(Board initialBoard, Board b, int playerNum, int depth) {
        int myScoreBefore = initialBoard.getScore(playerNum);
        int yourScoreBefore = initialBoard.getScore((playerNum % 2) + 1);
        int myScoreAfter = b.getScore(playerNum);
        int yourScoreAfter = b.getScore((playerNum % 2) + 1);
        int netScore = (myScoreAfter - myScoreBefore) - (yourScoreAfter - yourScoreBefore);
        if (myScoreAfter > 24) {
            return 100 + (100 * depth);
        }
        else if (yourScoreAfter > 24) {
            return -100 - (100 * depth);
        }
        return netScore;
    }
    
    public boolean gameEnded(Board initialBoard, Board b, int playerNum) {
        int myScoreBefore = initialBoard.getScore(playerNum);
        int yourScoreBefore = initialBoard.getScore((playerNum % 2) + 1);
        int myScoreAfter = b.getScore(playerNum);
        int yourScoreAfter = b.getScore((playerNum % 2) + 1);
        if (myScoreAfter > 24 || yourScoreAfter > 24 || (yourScoreAfter == 24 && myScoreAfter == yourScoreAfter)) {
            return true;
        }
        return false;
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
