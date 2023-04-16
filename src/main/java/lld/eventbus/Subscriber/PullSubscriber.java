package lld.eventbus.Subscriber;


import lld.eventbus.Event;
import lld.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class PullSubscriber extends Subscriber
{
    public PullSubscriber(int id)
    {
        super(id);
    }
    public void consume(String topic) throws Exception
    {
        CompletableFuture<ArrayList<Event>> completableFuture = EventBus.getInstance().consume(topic,this);
        ArrayList<Event> events = completableFuture.get();
        for(Event event : events)
        {
            event.display();
        }
    }
}