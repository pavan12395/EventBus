package lld.eventbus.Subscriber;


import lld.eventbus.Event;

public class PushSubscriber extends Subscriber
{
    public PushSubscriber(int id)
    {
        super(id);
    }
    public void inform(Event event)
    {
        System.out.println("Event recieved at Subscriber's end : "+this.id);
        event.display();
    }
}