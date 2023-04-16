package lld.eventbus;

import java.util.concurrent.CompletableFuture;

public class Producer
{
    public int id;
    public Producer(int id)
    {
        this.id = id;
    }
    public void produce(String topic,Event event) throws Exception
    {
        CompletableFuture<Void> completableFuture = EventBus.getInstance().produce(topic,event);
        completableFuture.get();
    }

}