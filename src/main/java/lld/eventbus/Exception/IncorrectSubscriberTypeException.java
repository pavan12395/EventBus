package lld.eventbus.Exception;

public class IncorrectSubscriberTypeException extends Exception
{
    public IncorrectSubscriberTypeException(String topic,Integer subscriberId)
    {
        super("Subscriber : "+subscriberId+" for Topic : "+topic+" Not Found");
    }
}