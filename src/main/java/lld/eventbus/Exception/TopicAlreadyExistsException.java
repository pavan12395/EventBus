package lld.eventbus.Exception;

public class TopicAlreadyExistsException extends Exception
{
    public TopicAlreadyExistsException(String topic)
    {
        super("Topic : "+topic+" Already Exists");
    }
}