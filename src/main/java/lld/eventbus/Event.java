package lld.eventbus;

import java.sql.Timestamp;
import java.time.Duration;

public class Event
{
    final int id;
    final String message;

    final Timestamp load_time;

    public Event(final int id,final String message)
    {
        this.id =id;
        this.message = message;
        this.load_time = new Timestamp(System.currentTimeMillis());
    }
    public void display()
    {
        System.out.println("Event Id : "+this.id+" Message : "+this.message);
    }
    public boolean isExpired(Duration d)
    {
        long seconds = d.toSeconds();
        long loadTimeSeconds = this.load_time.getSeconds();
        long currSeconds = new Timestamp(System.currentTimeMillis()).getSeconds();
        return loadTimeSeconds+seconds>=currSeconds;
    }
}

// objects of events immutable
//