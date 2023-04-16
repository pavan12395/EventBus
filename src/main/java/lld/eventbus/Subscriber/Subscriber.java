package lld.eventbus.Subscriber;

import lld.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public abstract class Subscriber
{
    public final int id;

    public Subscriber(final int id)
    {
        this.id=id;
    }
    public void register(String topic) throws Exception
    {
        EventBus.getInstance().addSubscriber(topic,this).get();
    }
    public void removeSubscription(String topic)
    {
        EventBus.getInstance().deleteSubscription(topic,this);
    }
}