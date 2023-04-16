package lld.eventbus.Exception;

public class EventAlreadyExistsException extends Exception
{
    public EventAlreadyExistsException(Integer id)
    {
        super("Event : "+id+" Already Exists");
    }
}