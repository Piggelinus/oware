
/**
 * When the house specification is not in the correct range: house 1..6, playerNum 1..2
 *
 * @author Steven Bradley
 * @version 1.0
 */
public class InvalidHouseException extends Exception
{
    public InvalidHouseException(String message)
    {
        super(message);
    }
}
