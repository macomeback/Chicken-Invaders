package main.game;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TaskScheduler{
private static ScheduledThreadPoolExecutor scheduler;
static {
    scheduler=new ScheduledThreadPoolExecutor(3);
    scheduler.setRemoveOnCancelPolicy(true);
}
public static ScheduledExecutorService getScheduler(){
    return scheduler;
}
private TaskScheduler(){

}
}
