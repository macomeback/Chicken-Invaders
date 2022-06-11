package main.game.network;
import main.game.TaskScheduler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

abstract class NetworkObject implements Runnable{
    private boolean isClosed=false;
    private ScheduledFuture<?> future;
    boolean isClosed(){
        return isClosed;
    }
    void close() {
        if(isClosed || future.isCancelled())
            return;
        isClosed=true;
        future.cancel(false);
    }
    void scheduleTask(){
        future= TaskScheduler.getScheduler().scheduleWithFixedDelay(this,0,25,TimeUnit.MILLISECONDS);
    }
}
