package lld.eventbus;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Janitor extends Thread
{
    private ConcurrentHashMap<String, ArrayList<Event>> eventMap;
    private Duration duration;

    private Duration period;
    public Janitor(final ConcurrentHashMap<String,ArrayList<Event>> eventMap,final Duration duration,final Duration period)
    {
        this.eventMap = eventMap;
        this.duration =duration;
        this.period = period;
    }
    public void clean()
    {
        for(String topic : eventMap.keySet())
        {
            ArrayList<Event> filteredEvents = new ArrayList<>();
            ArrayList<Event> cleanedEvents = new ArrayList<>();
            for(Event e : eventMap.get(topic))
            {
                if(!e.isExpired(this.duration))
                {
                    filteredEvents.add(e);
                }
                else
                {
                    cleanedEvents.add(e);
                }
            }
            display(topic,cleanedEvents);
            eventMap.put(topic,filteredEvents);
        }
    }
    public void display(String topic,ArrayList<Event> cleanedEvents)
    {
        if(cleanedEvents==null || cleanedEvents.size()==0){return;}
        System.out.println("Cleaned the Below Mentioned Events for the topic : "+topic);
        for(Event e : cleanedEvents)
        {
            e.display();
        }
    }
    public void run()
    {
        try
        {
            while(true) {
                Thread.sleep(period.toMillis());
                System.out.println("************** Janitor Started *******************");
                this.clean();
                System.out.println("**************  Janitor Ended  *******************");
            }
        }
        catch(InterruptedException e)
        {
            System.out.println("Error in Janitor");
        }
    }
}