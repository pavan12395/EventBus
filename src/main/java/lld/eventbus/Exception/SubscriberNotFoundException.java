package lld.eventbus.Exception;

public class SubscriberNotFoundException extends Exception
{
    public SubscriberNotFoundException(String topic,Integer subscriberId)
    {
        super("Subscriber : "+subscriberId+" for Topic : "+topic+" Not Found");
    }
}