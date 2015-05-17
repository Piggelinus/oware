import java.util.Comparator;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * GameManager objects allow the user to start, load, save and play game(s). In the main method, interaction with the game user is via the console.
 *
 * You need to provide a main method where the first command line parameter can optionally specify a saved game to load (@see #loadGame) and play (@see #playGame).
 *
 * If a game is not underway the user may start a new game by entering 'NEW _player1type_ _player2type_' where the player types are either 'Human' or 'Computer'. The user may optionally specify player names with a colon but no space. E.g. 'NEW Human:Steven Computer:Orac' will start a new game between a human player called 'Steven' and a computer player called 'Orac'.
 *
 * If a game is not underway the user may load a previously saved game by entering 'LOAD _fname_' where _fname_ is the name of a previously saved game file (@see #loadGame). The game is then restarted (@see #playGame).
 *
 * If a game is not underway but a game has been set up through LOAD or NEW or command line parameter then the game can be saved with 'SAVE _fname_'.
 *
 * If a game is not underway (e.g. because the game has finished) then the command 'EXIT' halts the program.

 * The GameManager also implements Comparator on Player by playing two games, with the two players taking turns to start. If the two games yield symmetric results (e.g. player 1 wins, player 2 wins) the the players are considered equal, otherwise the better player is better ...
 *
 * You need to provide a default constructor (no parameters) for a GameManager.
 *
 * @author Linus Ericsson
 * @version 0.1
 *
 */

public class GameManagerImpl implements GameManager {
    private GameImpl game;
    public static InputStream in = System.in;
    public static PrintStream out = System.out;

    public static void main(String[] args) {
        GameManager gm = new GameManagerImpl();
        if (args.length > 0) {
            try {
                gm.loadGame(args[0]);
            }
            catch (Exception e) {
                out.println(e.getMessage());
            }
        }
        gm.manage(System.in, System.out);
    }

    public GameManagerImpl() {

    }

    public GameManagerImpl(Player player1, Player player2) {
        game = new GameImpl(player1, player2);
    }

    /** @return The current game object **/
    public Game getGame() {
        return game;
    }

    /**
     * Load the game state from the given file.
     *
     * @param fname The name of the file to load.
     *
     * @throws FileFailedException If, for whatever reason, the game file could not be loaded.
     *
     */

    public void loadGame(String fname) throws FileFailedException {
        try {
            File file = new File(fname);
            Scanner reader = new Scanner(file);
            int[] scores = new int[2];
            int[] houses = new int[BoardImpl.NUMBER_OF_HOUSES];
            String playerTypes[] = new String[2];
            String names[] = new String[2];
            int currentPlayerNum = 0;
            int noScoreChangeCounter = 0;
            scores[0] = new Integer(reader.next());
            scores[1] = new Integer(reader.next());
            for (int i = 0; i < BoardImpl.NUMBER_OF_HOUSES; i++) {
                houses[i] = new Integer(reader.next());
            }
            playerTypes[0] = reader.next();
            names[0] = reader.next();
            playerTypes[1] = reader.next();
            names[1] = reader.next();
            currentPlayerNum = new Integer(reader.next());
            noScoreChangeCounter = new Integer(reader.next());
            Player[] players = new Player[2];
            for (int i = 0; i < 2; i++) {
                if (playerTypes[i].equals("0")) {
                    players[i] = new HumanPlayer(names[i]);
                }
                else {
                    players[i] = new ComputerPlayer(names[i]);
                }
            }
            ArrayList<String> previousBoardStates = new ArrayList<String>();
            while (reader.hasNext()) {
                String state = reader.next();
                String stateWithSpaces = "";
                String space = " ";
                for (int i = 0; i < state.length(); i++) {
                    stateWithSpaces += state.substring(i, i + 1);
                    if (i < state.length() - 1) {
                        stateWithSpaces += space;
                    }
                }
                previousBoardStates.add(stateWithSpaces);
            }
            game = new GameImpl(players[0], players[1]);
            players[0].setIn(this.in);
            players[1].setIn(this.in);
            players[0].setOut(this.out);
            players[1].setOut(this.out);
            game.setPreviousBoardStates(previousBoardStates);
            game.setBoard(houses, scores);
            game.setCurrentPlayerNum(currentPlayerNum);
            game.setNoScoreCounter(noScoreChangeCounter);
        }
        catch (Exception e) {
            throw new FileFailedException("Format of saved file not valid");
        }
        if(game.validateBoard()) {
            try {
                playGame();
            } catch (Exception e) {}
        }
        else {
            throw new FileFailedException("File does not contain valid game");
        }
    }

    public void loadGameWithoutPlaying(String fname) throws FileFailedException {
        try {
            File file = new File(fname);
            Scanner reader = new Scanner(file);
            int[] scores = new int[2];
            int[] houses = new int[BoardImpl.NUMBER_OF_HOUSES];
            String playerTypes[] = new String[2];
            String names[] = new String[2];
            int currentPlayerNum = 0;
            int noScoreChangeCounter = 0;
            scores[0] = new Integer(reader.next());
            scores[1] = new Integer(reader.next());
            for (int i = 0; i < BoardImpl.NUMBER_OF_HOUSES; i++) {
                houses[i] = new Integer(reader.next());
            }
            playerTypes[0] = reader.next();
            names[0] = reader.next();
            playerTypes[1] = reader.next();
            names[1] = reader.next();
            currentPlayerNum = new Integer(reader.next());
            noScoreChangeCounter = new Integer(reader.next());
            Player[] players = new Player[2];
            for (int i = 0; i < 2; i++) {
                if (playerTypes[i].equals("0")) {
                    players[i] = new HumanPlayer(names[i]);
                }
                else {
                    players[i] = new ComputerPlayer(names[i]);
                }
            }
            ArrayList<String> previousBoardStates = new ArrayList<String>();
            while (reader.hasNext()) {
                String state = reader.next();
                String stateWithSpaces = "";
                String space = " ";
                for (int i = 0; i < state.length(); i++) {
                    stateWithSpaces += state.substring(i, i + 1);
                    if (i < state.length() - 1) {
                        stateWithSpaces += space;
                    }
                }
                previousBoardStates.add(stateWithSpaces);
            }
            game = new GameImpl(players[0], players[1]);
            game.setPreviousBoardStates(previousBoardStates);
            game.setBoard(houses, scores);
            game.setCurrentPlayerNum(currentPlayerNum);
            game.setNoScoreCounter(noScoreChangeCounter);
        }
        catch (Exception e) {
            throw new FileFailedException("Format of saved file not valid");
        }
        if(!game.validateBoard()) {
            throw new FileFailedException("File does not contain valid game");
        }
    }

    /**
     * Save the game state to the given file.
     *
     * @param fname The name of the file to save.
     *
     * @throws FileFailedException If, for whatever reason, the game file could not be saved (including file already exists).
     *
     */

    public void saveGame(String fname) throws FileFailedException {
        if (game != null) {
            try {
                File file = new File(fname);
                FileWriter writer = new FileWriter(file);
                String gameState = game.getGameState();
                writer.write(gameState);
                writer.close();
            }
            catch (Exception e) {
                out.println(e.getMessage());
            }
        }
        else {
            throw new FileFailedException("No game object found");
        }
    }

    public void newGame(Scanner scanner) {
        Player[] players = new Player[2];
        String[] playerInfo = new String[2];
        for (int i = 0; i < playerInfo.length; i++) {
            String name = "";
            playerInfo[i] = scanner.next();
            if (playerInfo[i].contains(":")) {
                String[] info = playerInfo[i].split(":");
                if (info[0].toLowerCase().equals("human")) {
                    if (info.length > 1) {
                        name = info[1];
                    }
                    players[i] = new HumanPlayer(name);
                }
                else if (info[0].toLowerCase().equals("computer")) {
                    int difficulty = 3;
                    if (info.length > 1) {
                        name = info[1];
                        if (name.contains("(")) {
                            String[] nameAndDifficulty = name.split("\\(");
                            name = nameAndDifficulty[0];
                            try {
                                difficulty = new Integer(nameAndDifficulty[1].replaceAll("\\)", ""));
                            } catch (Exception e) {}
                        }
                    }
                    players[i] = new ComputerPlayer(name, difficulty);
                }
            }
            else {
                if (playerInfo[i].toLowerCase().equals("human")) {
                    players[i] = new HumanPlayer(name);
                }
                else if (playerInfo[i].toLowerCase().equals("computer")) {
                    players[i] = new ComputerPlayer(name);
                }
            }
        }
        try {
            game = new GameImpl(players[0], players[1]);
            players[0].setIn(this.in);
            players[1].setIn(this.in);
            players[0].setOut(this.out);
            players[1].setOut(this.out);
            playGame();
        }
        catch (Exception e) {

        }
    }

    public void newGame(Player player1, Player player2) {
        try {
            game = new GameImpl(player1, player2);
            player1.setIn(this.in);
            player2.setIn(this.in);
            player1.setOut(this.out);
            player2.setOut(this.out);
            playGame();
        }
        catch (Exception e) {

        }
    }

    public void newGameWithoutPlaying(Player player1, Player player2) {
        try {
            game = new GameImpl(player1, player2);
        }
        catch (Exception e) {

        }
    }

    /**
     * Play the current game to completion, returning the playerNum of the winning player (1..2) or 0 if the game ends in a draw. Input is taken from in (@see #manage). If a computer player quits the game then they lose. If a computer player takes longer than one second for any move then it loses. After each turn is taken the game state (toString()) is sent to out.

     * @throws QuitGameException If a human player quits the game via QuitGameException.
     **/

    public int playGame() throws QuitGameException {
        int winner = -1;
        while (game.getPlaying()) {
            try {
                game.nextMove();
            }
            catch (InvalidMoveException e) {
                out.println(e.getMessage());
                break;
            }
            catch (InvalidHouseException e) {
                out.println(e.getMessage());
                break;
            }
            winner = game.checkWin();
            game.saveBoardState();
        }
        out.println("Game has ended");
        if (winner == 0) {
            out.println("It's a tie!");
        }
        else {
            out.println(game.namePlayer(winner) + " wins!");
        }
        return winner;
    }

    /**
     * accept input commands, including LOAD, SAVE, NEW, EXIT from the specified InputStream. All output is sent to the specified PrintStream. Can be used for testing the gameManager class via predefined inputs (e.g. a file) and writing the output to file. The specified input stream and output streams are also used for any player move input and output (i.e. selecting moves and QUIT). If a result is achieved this should be announced to the user.
     *
     * @param in The InputStream to be used for setting up and playing the game: System.in when not testing.
     *
     * @param out The OutputStream to be used for sending messages to the user while setting up and playing the game: System.out when not testing.
     *
     * @return The Game state after the instructions have been followed.
     **/
    public Game manage(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
        Scanner scanner = new Scanner(in);
        out.println("Welcome!");
        out.println("Available commands:");
        out.println("LOAD <file_name>");
        out.println("SAVE <file_name>");
        out.println("NEW <player_type>(:<player_name>) <player_type>(:<player_name>)");
        out.println("EXIT: close game");
        while (true) {
            String input = scanner.next();
            if (input.toLowerCase().equals("load")) {
                try {
                    loadGame(scanner.next());
                } catch (Exception e) { out.println(e.getMessage()); }
            }
            else if (input.toLowerCase().equals("save")) {
                try {
                    saveGame(scanner.next());
                } catch (Exception e) { out.println(e.getMessage()); }
            }
            else if (input.toLowerCase().equals("new")) {
                newGame(scanner);
            }
            else if (input.toLowerCase().equals("play")) {
                String turn = scanner.next();
                Player player1 = null;
                Player player2 = null;
                if (turn.equals("1")) {
                    player1 = new HumanPlayer("Linus");
                    player2 = new ComputerPlayer("AI");
                }
                else if (turn.equals("2")) {
                    player1 = new ComputerPlayer("AI");
                    player2 = new HumanPlayer("Linus");
                }
                newGame(player1, player2);
            }
            else if (input.toLowerCase().equals("compare")) {
                try {
                    int a = new Integer(scanner.next());
                    int b = new Integer(scanner.next());
                    int result = compare(new ComputerPlayer("a", a), new ComputerPlayer("b", b));
                    if (result == 1) {
                        GameManagerImpl.out.println("Player a (" + a + ") has defeated Player b (" + b + ")");
                    }
                    else if (result == -1) {
                        GameManagerImpl.out.println("Player b (" + b + ") has defeated Player a (" + a + ")");
                    }
                    else {
                        GameManagerImpl.out.println("Player a (" + a + ") and Player b (" + b + ") have tied");
                    }
                }
                catch (Exception e) {}
            }
            else if (input.toLowerCase().equals("tournament")) {
                tournament();
            }/*
            else if (input.toLowerCase().equals("test")) {
            Player player = null;
            String playerInfo = scanner.next();
            String name = "";
            if (playerInfo.contains(":")) {
            String[] info = playerInfo.split(":");
            if (info[0].toLowerCase().equals("human")) {
            if (info.length > 1) {
            name = info[1];
            }
            player = new HumanPlayer(name);
            }
            else if (info[0].toLowerCase().equals("computer")) {
            int difficulty = 3;
            if (info.length > 1) {
            name = info[1];
            if (name.contains("(")) {
            String[] nameAndDifficulty = name.split("\\(");
            name = nameAndDifficulty[0];
            try {
            difficulty = new Integer(nameAndDifficulty[1].replaceAll("\\)", ""));
            } catch (Exception e) {}
            }
            }
            player = new ComputerPlayer(name, difficulty);
            }
            }
            else {
            if (playerInfo.toLowerCase().equals("human")) {
            player = new HumanPlayer(name);
            }
            else if (playerInfo.toLowerCase().equals("computer")) {
            player = new ComputerPlayer(name);
            }
            }
            if (name == "") {
            name = "Your champion";
            }
            int matches = new Integer(scanner.next());
            int startingNum = 1;
            if (scanner.next().equals("2")) {
            startingNum = 2;
            }
            boolean doubleMatch = true;
            if (scanner.next().equals("0")) {
            doubleMatch = false;
            }
            testPlayer(player, matches, name, startingNum, doubleMatch);
            }
            else if (input.toLowerCase().equals("testmove")) {
            testMove(new Integer(scanner.next()));
            }*/
            else if (input.toLowerCase().equals("set")) {
                try {
                    BoardImpl.NUMBER_OF_HOUSES = new Integer(scanner.next());
                    BoardImpl.SEEDS_IN_HOUSES = new Integer(scanner.next());
                } catch (Exception e) {}
            }
            else if (input.toLowerCase().equals("exit")) {
                break;
            }
        }
        return game;
    }

    /*
    public void testMove(int matches) {
    for (int move = 1; move <= BoardImpl.NUMBER_OF_HOUSES / 2; move++) {
    Board board = new BoardImpl();
    try {
    board.makeMove(move, 1);
    } catch (Exception e) {}
    String name = "AI";
    int wins = 0;
    for (int i = 0; i < matches; i++) {
    try {
    game = new GameImpl(new RandomPlayer("Challenger"), new RandomPlayer(name));
    game.setBoard(board.clone());
    if (playGame() == 2) {
    wins++;
    }
    } catch (Exception e) {}
    }
    out.println(name + " has won " + wins + " out of " + matches + " with starting move " + move);
    }
    }

    public int testPlayer(Player player, int matches, String name, int startingNum, boolean doubleMatch) {
    int wins = 0;
    int ties = 0;
    for (int i = 0; i < matches; i++) {
    int result = 0;
    if (doubleMatch) {
    result = compare(player, new RandomPlayer("Challenger"));
    }
    else {
    game = new GameImpl(player, new RandomPlayer("Challenger"));
    try {
    result = playGame();
    } catch (Exception e) {}
    }
    if (result == 1) {
    wins++;
    }
    if (result == 0) {
    ties++;
    }
    }
    out.println(matches + " games played");
    out.println(wins + " games won");
    out.println(ties + " games tied");
    out.println((matches - wins - ties) + " games lost");
    return wins;
    }
     */
    public void tournament() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (int i = 1; i <= 8; i++) {
            players.add(new ComputerPlayer(Integer.toString(i), i));
        }
        Collections.sort(players, new GameManagerImpl());
        out.println(players);
        out.println("Winner: " + players.get(players.size() - 1));
    }

    public int compare(Player p, Player q) {
        p.setIn(this.in);
        q.setIn(this.in);
        p.setOut(this.out);
        q.setOut(this.out);
        int numOfGames = 2;
        int[] results = new int[numOfGames];
        game = new GameImpl(p, q);
        try {
            results[0] = playGame();
        }
        catch (Exception e) {}
        game = new GameImpl(q, p);
        try {
            results[1] = playGame();
            if (results[1] != 0) {
                results[1] = (results[1] % 2) + 1;
            }
        }
        catch (Exception e) {}
        if (results[0] == 1) {
            if (results[1] != 2) {
                return 1;
            }
            else {
                return 0;
            }
        }
        if (results[0] == 2) {
            if (results[1] != 1) {
                return -1;
            }
            else {
                return 0;
            }
        }
        else {
            if (results[1] == 1) {
                return 1;
            }
            else if (results[1] == 2) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }
}
