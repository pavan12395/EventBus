package lld.eventbus;

import lld.eventbus.Subscriber.PullSubscriber;

public class Main {
    public static void main(String args[]) throws Exception
    {
        EventBus.getInstance().createTopic("hello");
        EventBus.getInstance().createTopic("pavan");
        Producer p = new Producer(1);
        Event event =new Event(1,"Hello!");
        p.produce("hello",event);
        p.produce("pavan",event);
        Thread.sleep(20000);
        PullSubscriber pullSubscriber = new PullSubscriber(1);
        pullSubscriber.register("hello");
        System.out.println("The consumed events are");
        pullSubscriber.consume("hello");
    }
}

/*
RetryAlgorithm
How to deal with past messages in the Queue for Push Subscribers (Read)
Time complexity analysis & Cleaning the code
logging
 */