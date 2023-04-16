package lld.eventbus;

import lld.eventbus.Event;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class KeyedExecutor {
    private Executor[] executors;

    public KeyedExecutor(int threads) {
        executors = new Executor[threads];
        for(int i=0;i<executors.length;i++)
        {
            executors[i]=Executors.newSingleThreadExecutor();
        }
    }

    public CompletableFuture<Void> submit(int id,Runnable r)
    {
        return CompletableFuture.runAsync(r,executors[id]).toCompletableFuture();
    }
    public  CompletableFuture<ArrayList<Event>> submit(int id,Supplier<ArrayList<Event>> r)
    {
        return CompletableFuture.supplyAsync(r,executors[id]).toCompletableFuture();
    }
    public Integer size()
    {
        return executors.length;
    }
}