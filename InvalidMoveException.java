/**
 * When a move is against the rules, because of starvation of the opponent.
 * @author Steven Bradley
 * @version 1.0
 * 
 */
public class InvalidMoveException extends Exception
{
    public InvalidMoveException(String message){
        super(message);
    }
}
