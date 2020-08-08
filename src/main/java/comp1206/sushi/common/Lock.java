package comp1206.sushi.common;


import java.io.Serializable;

/**
 * Same as java.util.concurrent.locks just made it Serializable
 https://stackoverflow.com/questions/24020379/alternative-to-lock-trylock-in-java-1-4
 */
public final class Lock implements Serializable {
    private Thread owner;
    private int nestCount;

    public synchronized void lock() throws InterruptedException {
        for(;;) {
            if(tryLock()) return;
            wait();
        }
    }
    public synchronized boolean tryLock() {
        Thread me=Thread.currentThread();
        if(owner!=me) {
            if(nestCount!=0) return false;
            owner=me;
        }
        nestCount++;
        return true;
    }
    public synchronized void unlock() {
        if(owner!=Thread.currentThread())
            throw new IllegalMonitorStateException();
        if(--nestCount == 0) {
            owner=null;
            notify();
        }
    }
}