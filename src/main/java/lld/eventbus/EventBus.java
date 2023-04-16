package lld.eventbus;


import lld.eventbus.Exception.*;
import lld.eventbus.Subscriber.Subscriber;
import lld.eventbus.Subscriber.PushSubscriber;
import lld.eventbus.Subscriber.PullSubscriber;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.sql.Timestamp;

public class EventBus {
    public KeyedExecutor subscriberThreads;

    public KeyedExecutor publisherThreads;

    //maintain the events of each topic
    public ConcurrentHashMap<String, ArrayList<Event>> eventsMap;
    //maintains topic --> subscriber --> offset.
    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, Timestamp>> eventIdsMap;

    public ConcurrentHashMap<String, Set<Subscriber>> subscriberHashMap;
    public static EventBus instance;

    public Janitor janitor;

    public static EventBus getInstance() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
                return instance;
            }
        }
        return instance;
    }

    public EventBus() {
        this.subscriberThreads = new KeyedExecutor(4);
        this.publisherThreads = new KeyedExecutor(4);
        this.eventsMap = new ConcurrentHashMap<>();
        this.eventIdsMap = new ConcurrentHashMap<>();
        this.subscriberHashMap = new ConcurrentHashMap<>();
        janitor = new Janitor(this.eventsMap,Duration.ofSeconds(15),Duration.ofSeconds(10));
        janitor.start();
    }

    public void createTopic(String topic) throws Exception {
        if (this.containsTopic(topic)) {
            throw new TopicAlreadyExistsException(topic);
        } else {
            eventsMap.put(topic, new ArrayList<>());
            eventIdsMap.put(topic, new ConcurrentHashMap<>());
            subscriberHashMap.put(topic,new HashSet<>());
        }
    }

    public boolean containsTopic(String topic) {
        return eventsMap.containsKey(topic);
    }
    public boolean containsSubscriber(String topic,Subscriber s)
    {
        return subscriberHashMap.get(topic).contains(s);
    }
    public boolean containsEvent(String topic,Event e)
    {
        for(Event event : eventsMap.get(topic))
        {
            if(event.id == e.id)
            {
                return true;
            }
        }
        return false;
    }
    public CompletableFuture<Void> produce(String topic, Event event) throws Exception {
        if (!this.containsTopic(topic)) {
            throw new TopicNotFoundException(topic);
        }
        if(this.containsEvent(topic,event))
        {
            throw new EventAlreadyExistsException(event.id);
        }
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                eventsMap.get(topic).add(event);
                CompletableFuture.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        for(Subscriber s : subscriberHashMap.get(topic))
                        {
                            if(s instanceof PushSubscriber)
                            {
                                ((PushSubscriber) s).inform(event);
                            }
                        }
                    }
                });
            }
        };
        int id = Math.abs(topic.hashCode()) % (publisherThreads.size());
        return publisherThreads.submit(id, myRunnable);
    }
    //produce :- O(1)
    public CompletableFuture<Void> addSubscriber(String topic, Subscriber s) throws Exception {
        if (!this.containsTopic(topic)) {
            throw new TopicNotFoundException(topic);
        }
        if(this.containsSubscriber(topic,s)){
            throw new SubscriberAlreadyExists(topic,s.id);
        }
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                subscriberHashMap.get(topic).add(s);
                if(s instanceof PullSubscriber)
                {
                    eventIdsMap.get(topic).put(s.id, new Timestamp(0));
                }
            }
        };
        int id = Math.abs(topic.hashCode()) % (subscriberThreads.size());
        return subscriberThreads.submit(id, myRunnable);
    }

    //consume :- O(n)
    public CompletableFuture<ArrayList<Event>> consume(String topic, Subscriber s) throws Exception {
        Integer subscriberId = s.id;
        if (!this.containsTopic(topic)) {
            throw new TopicNotFoundException(topic);
        }
        if(!this.containsSubscriber(topic,s))
        {
            throw new SubscriberNotFoundException(topic,subscriberId);
        }
        if(!(s instanceof PullSubscriber))
        {
            throw new IncorrectSubscriberTypeException(topic,subscriberId);
        }
        Supplier<ArrayList<Event>> mySupplier = new Supplier<ArrayList<Event>>() {
            @Override
            public ArrayList<Event> get() {
                ArrayList<Event> result = new ArrayList<>();
                Timestamp currEventTimeStamp = eventIdsMap.get(topic).get(subscriberId);
                ArrayList<Event> events = eventsMap.get(topic);
                Timestamp resultantTimeStamp = currEventTimeStamp;
                for(Event event : events)
                {
                    if(currEventTimeStamp.compareTo(event.load_time)<0)
                    {
                        resultantTimeStamp = event.load_time;
                        result.add(event);
                    }
                }
                eventIdsMap.get(topic).put(subscriberId, resultantTimeStamp);
                return result;
            }
        };
        String key = String.valueOf(subscriberId) + topic;
        int id = Math.abs(key.hashCode()) % (subscriberThreads.size());
        return subscriberThreads.submit(id, mySupplier);
    }
    public void deleteSubscription(String topic,Subscriber s)
    {
       subscriberHashMap.get(topic).remove(s);
       if(s instanceof PullSubscriber)
       {
           eventIdsMap.get(topic).remove(s.id);
       }
    }
}