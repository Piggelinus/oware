import java.util.*;
import java.io.FileWriter;
import java.io.File;

public class KnowledgeBase {
    
    final static String FILE_NAME = "knowledge-base.txt";
    
    private HashMap<String, Integer> tree;
    private Board currentBoard;
    private int currentPlayer;
    public static int MAXIMUM_RECURSION_DEPTH = 100;
    
    public static void main(String[] args) {
        KnowledgeBase base = new KnowledgeBase();
    }
    
    public KnowledgeBase() {
        tree = new LinkedHashMap<String, Integer>();
        Board board = new BoardImpl();
        currentBoard = board;
        currentPlayer = 1;
        int depth = 1;
        try {
            loadKnowledgeBase(FILE_NAME);
        } catch (Exception e) {}
        boolean done = false;
        while (!done) {
            try {
                /*
                board.makeMove(1, 1);
                currentPlayer = (currentPlayer % 2) + 1;
                 */
                tree.put(fullBoardState(board, currentPlayer), 0);
                search(currentBoard, currentPlayer, depth);
                done = true;
            } catch (StackOverflowError e) {
                GameManagerImpl.out.println("Stack Overflow Error halted the process");
            } catch (OutOfMemoryError e) {
                GameManagerImpl.out.println("Process ran out of memory");
            } catch (Exception e) {}
        }
        try {
            saveKnowledgeBase(FILE_NAME);
        }  catch (Exception f) {}
    }
    
    public int search(Board board, int playerNum, int depth) {
        if (depth > MAXIMUM_RECURSION_DEPTH) {
            return 0;
        }
        else if (depth == 2) {
            GameManagerImpl.out.println(board);
        }
        else {
            //GameManagerImpl.out.print(depth + " ");
        }
        int otherPlayerNum = ((playerNum % 2) + 1);
        currentBoard = board;
        currentPlayer = playerNum;
        String boardState = fullBoardState(board, playerNum);
        List<Integer> results = new ArrayList<Integer>();
        List<Integer> possibleMoves = getPossibleMoves(board, playerNum);
        if (possibleMoves.size() > 0) {
            for (int move : possibleMoves) {
                Board b_clone = board.clone();
                move(b_clone, move, playerNum);
                //GameManagerImpl.out.println("depth : " + depth);
                //GameManagerImpl.out.println("move : " + move);
                if (!tree.containsKey(fullBoardState(b_clone, otherPlayerNum))) {
                    /* if this is the first time we've encountered this game state */
                    tree.put(fullBoardState(b_clone, otherPlayerNum), 0);
                    //GameManagerImpl.out.println(b_clone);
                    int winner = gameEnded(fullBoardState(b_clone, otherPlayerNum), checkWin(b_clone), results, "NORMAL");
                    /* if player wins in normal way */
                    if (winner > 0) {
                        if (winner == 3) {
                            results.add(winner);
                        }
                        else {
                            results.add((winner % 2) + 1);
                        }
                    }
                    else { /* or if game has not ended */
                        results.add(search(b_clone.clone(), otherPlayerNum, depth + 1));
                    }
                }
                else {
                    /* if game state has already happened and been saved */
                    endAndReap(b_clone);
                    //GameManagerImpl.out.println("board repeated");
                    //GameManagerImpl.out.println(b_clone);
                    int winner = gameEnded(fullBoardState(b_clone, otherPlayerNum), checkWin(b_clone), results, "REPEATED");
                    /* if player wins in normal way */
                    if (winner == 3) {
                        results.add(winner);
                    }
                    else {
                        results.add((winner % 2) + 1);
                    }
                }
            }
        }
        else {
            /* if starvation occurs */
            endAndReap(board);
            //GameManagerImpl.out.println("depth : " + depth);
            //GameManagerImpl.out.println("no move available");
            //GameManagerImpl.out.println("starvation");
            //GameManagerImpl.out.println(board);
            return gameEnded(boardState, checkWin(board), results, "STARVATION");
        }
        //GameManagerImpl.out.println("reached end of " + boardState);
        return inheritState(boardState, results);
    }
    
    public int gameEnded(String boardState, int winner, List<Integer> children, String endingWay) {
        //GameManagerImpl.out.println(winner);
        if (winner > 0) {
            tree.put(boardState, winner);
            children.add(winner);
            //GameManagerImpl.out.println("Game ended");
            //GameManagerImpl.out.println("SAVING " + endingWay + " STATE FOR GAME " + boardState + " WITH RESULTS " + children);
            return winner;
        }
        return 0;
    }
    
    public int inheritState(String boardState, List<Integer> children) {
        //GameManagerImpl.out.println("SAVING INHERITED STATE FOR GAME " + boardState + " WITH RESULTS " + children);
        if (children.contains(2)) {
            tree.put(boardState, 1);
            return 1;
        }
        else if (children.contains(3)) {
            tree.put(boardState, 3);
            return 3;
        }
        else if (children.contains(1)) {
            tree.put(boardState, 2);
            return 2;
        }
        else {
            tree.put(boardState, 0);
            return 0;
        }
    }
    
    public int checkWin(Board board) {
        int winLevel = BoardImpl.SEEDS_IN_HOUSES * (BoardImpl.NUMBER_OF_HOUSES / 2);
        if (board.getScore(1) > winLevel) {
            return 1;
        }
        else if (board.getScore(2) > winLevel) {
            return 2;
        }
        else if (board.getScore(1) == board.getScore(2) && board.getScore(1) == winLevel) {
            return 3;
        }
        else {
            return 0;
        }
    }
    
    public void move(Board board, int move, int playerNum) {
        try {
            board.makeMove(move, playerNum);
        } catch (Exception e) {}
    }
    
    public void endAndReap(Board board) {
        List<Integer> oneToSix = new ArrayList<Integer>(BoardImpl.NUMBER_OF_HOUSES / 2);
        for (int i = 1; i <= BoardImpl.NUMBER_OF_HOUSES / 2; i++) {
            oneToSix.add(i);
        }
        try {
            ((BoardImpl)board).reap(oneToSix, 1);
            ((BoardImpl)board).reap(oneToSix, 2);
        } catch (Exception e) {}
    }
    
    public String fullBoardState(Board board, int playerNum) {
        return playerNum + " " + ((BoardImpl)board).getBoardState();
    }
    
    public int checkMove(Board b, int house, int playerNum) throws InvalidHouseException, InvalidMoveException {
        b.makeMove(house, playerNum);
        int enemySeeds = 0;
        for (int i = 1; i <= BoardImpl.NUMBER_OF_HOUSES / 2; i++) {
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
        for (int i = 1; i <= BoardImpl.NUMBER_OF_HOUSES / 2; i++) {
            try {
                possibleMoves.add(checkMove(b.clone(), i, playerNum));
            }
            catch (Exception f) {}
        }
        return possibleMoves;
    }
    
    public void saveKnowledgeBase(String fileName) throws FileFailedException {
        if (tree.size() > 0) {
            try {
                File file = new File(fileName);
                FileWriter writer = new FileWriter(file);
                for (String boardState : tree.keySet()) {
                    int value = tree.get(boardState);
                    if (value == 1 || value == 3) {
                        String boardStateWithoutSpaces = boardState.replaceAll(" ", "");
                        writer.write(boardStateWithoutSpaces + " " + value + "\n");
                    }
                }
                writer.close();
            }
            catch (Exception e) {
                //GameManagerImpl.out.println(e.getMessage());
            }
        }
        else {
            throw new FileFailedException("Map is empty");
        }
    }
    
    public void loadKnowledgeBase(String fname) throws FileFailedException {
        try {
            File file = new File(fname);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                Scanner lineReader = new Scanner(reader.nextLine());
                String boardState = lineReader.next();
                int value = new Integer(lineReader.next());
                tree.put(boardState, value);
            }
        }
        catch (Exception e) {
            //GameManagerImpl.out.println(e.getMessage());
        }
    }
}