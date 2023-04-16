package lld.eventbus.Exception;

public class SubscriberAlreadyExists extends Exception
{
    public SubscriberAlreadyExists(String topic,Integer subscriberId)
    {
        super("Subscriber : "+subscriberId+" for Topic : "+topic+" Already Exists");
    }
}