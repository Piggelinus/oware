public class Master {
    
    public static void main(String[] args) {
        try {
            Master master = new Master(new Integer(args[0]), new Integer(args[1]), new Integer(args[2]), new Integer(args[3]), new Integer(args[4]));
        } catch (Exception e) {}
    }
    
    public Master(int numberOfHouses, int seedsInHouses, int maximumRecursionDepth, int matches, int startingNum) {
        initVariables(numberOfHouses, seedsInHouses, maximumRecursionDepth);
        KnowledgeBase base = new KnowledgeBase();
        GameManagerImpl gameManager = new GameManagerImpl();
        String name = "AI";
        gameManager.testPlayer(new ComputerPlayer(name), matches, name, startingNum, true);
        gameManager.manage(System.in, System.out);
    }
    
    public void initVariables(int numberOfHouses, int seedsInHouses, int maximumRecursionDepth) {
        BoardImpl.NUMBER_OF_HOUSES = numberOfHouses;
        BoardImpl.SEEDS_IN_HOUSES = seedsInHouses;
        ComputerPlayer.NUMBER_OF_HOUSES = numberOfHouses;
        ComputerPlayer.SEEDS_IN_HOUSES = seedsInHouses;
        HumanPlayer.NUMBER_OF_HOUSES = numberOfHouses;
        HumanPlayer.SEEDS_IN_HOUSES = seedsInHouses;
        RandomPlayer.NUMBER_OF_HOUSES = numberOfHouses;
        RandomPlayer.SEEDS_IN_HOUSES = seedsInHouses;
        KnowledgeBase.MAXIMUM_RECURSION_DEPTH = maximumRecursionDepth;
    }
}