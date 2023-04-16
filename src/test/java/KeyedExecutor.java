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

    public CompletionStage<Void> submit(MyRunnable r)
    {
        int id = r.id%(executors.length);
        return CompletableFuture.runAsync(r,executors[id]);
    }
    public  CompletionStage<String> submit(MySupplier<String> r)
    {
        int id = r.id%(executors.length);
        return CompletableFuture.supplyAsync(r,executors[id]);
    }
    public static void main(String args[]) throws Exception
    {
        MySupplier<String> mySupplier = new MySupplier<>(1,"helloworld");
        MySupplier<String> mySupplier1 = new MySupplier<>(2,"helloworld1");
        MySupplier<String> mySupplier2 = new MySupplier<>(3,"helloworld2");
        KeyedExecutor keyedExecutor = new KeyedExecutor(2);
        CompletableFuture<String> completableFuture = keyedExecutor.submit(mySupplier).toCompletableFuture();
        CompletableFuture<String> completableFuture1 = keyedExecutor.submit(mySupplier1).toCompletableFuture();
        CompletableFuture<String> completableFuture2 = keyedExecutor.submit(mySupplier2).toCompletableFuture();
        System.out.println(completableFuture.get());
        System.out.println(completableFuture1.get());
        System.out.println(completableFuture2.get());

    }
}

class MyRunnable implements Runnable
{
    public int id;
    public MyRunnable(int id)
    {
        this.id = id;
    }
    public void run()
    {
        System.out.println("Executing Runnable : "+this.id + " Executed by Thread : "+Thread.currentThread().getName());
    }
}

class MySupplier<T> implements Supplier<T> {
    public T name;

    public int id;
    public MySupplier(int id,T name)
    {
        this.id = id;
        this.name = name;
    }
    public T get()
    {
        System.out.println("Returning value from a Thread : "+Thread.currentThread().getName()+" ID : "+this.id);
        return name;
    }
}