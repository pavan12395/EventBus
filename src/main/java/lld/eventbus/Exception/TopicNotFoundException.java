package lld.eventbus.Exception;

public class TopicNotFoundException extends Exception
{
    public TopicNotFoundException(String topic)
    {
        super("Topic : "+topic+" Not Found");
    }
}