/**
 * Game objects represent the state of the game, including the board, the players, the current player
 * and the history of board states.
 *
 * Provide one constructor that makes a new game with the two players of the game as parameters.
 * Initialise the board state to have four seeds in each house.
 *
 * @author Linus Ericsson
 * @version 0.1
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GameImpl implements Game {
    
    private Player[] players;
    private int currentPlayerNum;
    private BoardImpl board;
    private ArrayList<String> previousBoardStates;
    private boolean playing;
    private int noScoreCounter = 0;
    private boolean noScoreChangeFlag = false;
    private boolean moveTimedOutFlag = false;
    private int timedOutPlayer = 0;
    private boolean starvationFlag = false;
    
    public static void main(String[] args) {
        GameImpl game = new GameImpl(new HumanPlayer(), new HumanPlayer());
        if (args.length > 0) {
            int[] houses = toArray(args[0], BoardImpl.NUMBER_OF_HOUSES);
            int[] scoreHouses = toArray(args[1], BoardImpl.NUMBER_OF_PLAYERS);
            game.setBoard(houses, scoreHouses);
        }
        try {
            game.play();
        }
        catch (Exception e) {}
    }
    
    public static int[] toArray(String str, int length) {
        Scanner sc = new Scanner(str);
        int[] array = new int[length];
        int i = 0;
        while (sc.hasNext()) {
            try {
                array[i] = Integer.parseInt(sc.next());
                i++;
            }
            catch (NumberFormatException nfe) {}
        }
        return array;
    }
    
    public GameImpl(Player player1, Player player2) {
        players = new Player[BoardImpl.NUMBER_OF_PLAYERS];
        players[0] = player1;
        players[1] = player2;
        currentPlayerNum = 1;
        previousBoardStates = new ArrayList<String>();
        board = new BoardImpl();
        playing = true;
    }
    
    public void setBoard(int[] houses, int[] scoreHouses) {
        board = new BoardImpl(houses, scoreHouses);
    }
    
    public void setBoard(Board board) {
        this.board = (BoardImpl)board;
    }
    
    public void incrementCounter() {
        noScoreCounter++;
    }
    
    public int getNoScoreCounter() {
        return noScoreCounter;
    }
    
    public void setNoScoreCounter(int noScoreCounter) {
        this.noScoreCounter = noScoreCounter;
    }
    
    public void resetNoScoreCounter() {
        noScoreCounter = 0;
    }
    
    public boolean validateBoard() {
        int totalNumberOfSeeds = 0;
        for (int i = 1; i <= BoardImpl.NUMBER_OF_HOUSES; i++) {
            try {
                totalNumberOfSeeds += board.getSeeds(board.houseParam(i), board.playerNumParam(1, i));
            } catch (Exception e) {
                GameManagerImpl.out.println("Something went wrong when validating the file");
            }
        }
        totalNumberOfSeeds += board.getScore(1) + board.getScore(2);
        if (totalNumberOfSeeds == BoardImpl.NUMBER_OF_HOUSES * BoardImpl.SEEDS_IN_HOUSES) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public int play() throws QuitGameException {
        int winner = -1;
        while (playing) {
            try {
                nextMove();
            }
            catch (InvalidMoveException e) {
                break;
            }
            catch (InvalidHouseException e) {
                break;
            }
            winner = checkWin();
            saveBoardState();
        }
        return winner;
    }
    
    public Player getCurrentPlayer() {
        return players[currentPlayerNum - 1];
    }
    
    public int getCurrentPlayerNum() {
        return currentPlayerNum;
    }
    
    public void setCurrentPlayerNum(int playerNum) {
        currentPlayerNum = playerNum;
    }
    
    public static int otherPlayerNum(int playerNum) {
        return (playerNum % 2) + 1;
    }
    
    public Board getCurrentBoard() {
        return board;
    }
    
    public int checkWin() {
        if (noScoreChangeFlag) {
            playing = false;
            int playerOneScore = board.getScore(1);
            int playerTwoScore = board.getScore(2);
            board.clearBoard();
            GameManagerImpl.out.println(board);
            if (playerOneScore > playerTwoScore) {
                return 1;
            }
            else if (playerTwoScore > playerOneScore) {
                return 2;
            }
            else {
                return 0;
            }
        }
        else if (moveTimedOutFlag) {
            playing = false;
            int otherPlayer = otherPlayerNum(timedOutPlayer);
            board.reapAll(otherPlayer);
            board.clearBoard();
            GameManagerImpl.out.println(board);
            GameManagerImpl.out.println("Move timed out");
            return otherPlayer;
        }
        else if (starvationFlag) {
            playing = false;
            board.reapAll();
            board.clearBoard();
            GameManagerImpl.out.println(board);
            GameManagerImpl.out.println("Starvation");
            return getResult();
        }
        else if (positionRepeated()) {
            playing = false;
            board.reapAll();
            board.clearBoard();
            GameManagerImpl.out.println(board);
            GameManagerImpl.out.println("Repeated board state");
            return getResult();
        }
        else {
            int winner = getResult();
            if (winner >= 0) {
                board.clearBoard();
                GameManagerImpl.out.println(board);
                playing = false;
            }
            return winner;
        }
    }
    
    public String namePlayer(int playerNum) {
        String name = "";
        try {
            name = ((HumanPlayer)players[playerNum - 1]).getName();
        }
        catch (Exception e) {
            try {
                name = ((ComputerPlayer)players[playerNum - 1]).getName();
            } catch (Exception f) { name = "Player " + Integer.toString(playerNum); }
        }
        return name;
    }
    
    /**
     @return 1 or 2 corresponding to the winning player, if the game is over and won. Return 0 if the game is over and it is a draw. Return a negative value if the game is not over.
     **/
    
    public int getResult() {
        int winLevel = BoardImpl.SEEDS_IN_HOUSES * (BoardImpl.NUMBER_OF_HOUSES / 2);
        for (int i = 1; i <= BoardImpl.NUMBER_OF_PLAYERS; i++) {
            if (board.getScore(i) > winLevel) {
                return i;
            }
        }
        int playerOneScore = board.getScore(1);
        int playerTwoScore = board.getScore(2);
        if (playerOneScore == playerTwoScore && playerOneScore == winLevel) {
            return 0;
        }
        return -1;
    }
    
    public void setPlaying(boolean bool) {
        playing = bool;
    }
    
    public boolean getPlaying() {
        return playing;
    }
    
    public String getGameState() {
        String space = " ";
        String gameState = board.getBoardState();
        gameState += players[0] + space;
        gameState += players[1] + space;
        gameState += currentPlayerNum + space;
        gameState += noScoreCounter;
        for (int i = 0; i < previousBoardStates.size(); i++) {
            gameState += space + previousBoardStates.get(i);
        }
        return gameState;
    }
    
    public void saveBoardState() {
        previousBoardStates.add(board.getBoardState().replaceAll(" ", ""));
    }
    
    public void setPreviousBoardStates(ArrayList<String> boardStates) {
        previousBoardStates = new ArrayList<String>(boardStates);
    }
    
    public ArrayList<String> getPreviousBoardStates() {
        return previousBoardStates;
    }
    
    /**
     * @return true if the current board position has occurred before in the history of the game.
     **/
    
    public boolean positionRepeated() {
        for (String previousBoardState : previousBoardStates) {
            if (previousBoardState.equals(board.getBoardState().replaceAll(" ", ""))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get a move from the current player and update the board and current player
    **/
    public void nextMove() throws InvalidHouseException, InvalidMoveException, QuitGameException {
        long startTime = System.currentTimeMillis();
        int house = players[currentPlayerNum - 1].getMove(board.clone(), currentPlayerNum);
        long endTime = System.currentTimeMillis();
        if (endTime - startTime > 1000 && players[currentPlayerNum - 1].isComputer()) {
            moveTimedOut(currentPlayerNum);
        }
        else {
            if (!moveAndCheckScoreChange(house, currentPlayerNum)) {
                currentPlayerNum = otherPlayerNum(currentPlayerNum);
                starvationCheck(currentPlayerNum);
            }
        }
    }
    
    
    public void starvationCheck(int playerNum) {
        boolean possibleMove = false;
        for (int i = 1; i <= BoardImpl.NUMBER_OF_HOUSES / 2; i++) {
            try {
                checkMove(board.clone(), i, playerNum);
                possibleMove = true;
            }
            catch (Exception f) {}
        }
        if (!possibleMove) {
            board.reapAll();
            board.clearBoard();
            GameManagerImpl.out.println(board);
            starvationFlag = true;
        }
    }
    
    public int checkMove(Board board, int house, int playerNum) throws InvalidHouseException, InvalidMoveException {
        board.makeMove(house, playerNum);
        int enemySeeds = 0;
        for (int i = 1; i <= BoardImpl.NUMBER_OF_HOUSES / 2; i++) {
            enemySeeds += board.getSeeds(i, otherPlayerNum(playerNum));
        }
        if (enemySeeds <= 0) {
            throw new InvalidMoveException("Invalid input: must sow seeds on opponents side");
        }
        else {
            return house;
        }
    }
    
    public boolean moveAndCheckScoreChange(int house, int currentPlayerNum) {
        int playerOneScoreBefore = board.getScore(1);
        int playerTwoScoreBefore = board.getScore(2);
        try {
            board.makeMove(house, currentPlayerNum);
        } catch (Exception e) {
            GameManagerImpl.out.println(e.getMessage());
        }
        int playerOneScoreAfter = board.getScore(1);
        int playerTwoScoreAfter = board.getScore(2);
        if (playerOneScoreBefore == playerOneScoreAfter && playerTwoScoreBefore == playerTwoScoreAfter) {
            incrementCounter();
            if (getNoScoreCounter() >= 100) {
                GameManagerImpl.out.println("No score change in 100 moves");
                noScoreChangeFlag = true;
                return true;
            }
        }
        else {
            resetNoScoreCounter();
        }
        return false;
    }
    
    public void moveTimedOut(int currentPlayerNum) {
        moveTimedOutFlag = true;
        timedOutPlayer = currentPlayerNum;
    }
    
    /**
     * override the toString method to provide a summary of the game state (including the board)
     **/
    public String toString() {
        return getGameState();
    }
    
    @Override
    public boolean equals(Object o) {
        Game game = (Game)o;
        if (this.toString().equals(game.toString())) {
            return true;
        }
        else {
            return false;
        }
    }
}