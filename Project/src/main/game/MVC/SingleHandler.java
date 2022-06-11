package main.game.MVC;

import main.game.TaskScheduler;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class SingleHandler implements Runnable {
   private Logic logic;
   private boolean isPaused;
   private ScheduledFuture<?> future;
    void startGame(boolean isNew){
        if(isNew)
            InfoHandler.getInfoHandler().addUserInfo((Map<String,?>)null);
            logic=Logic.newLogic(InfoHandler.getInfoHandler().userInfo(),View.getView().getCurrentUser(),4);
        future= TaskScheduler.getScheduler().scheduleWithFixedDelay(this,0,25, TimeUnit.MILLISECONDS);
    }
void cancel(){
        if(future!=null && !future.isCancelled())
            future.cancel(false);
    }
    Map<String,?> getUserMap(){
        if(logic!=null)
        return logic.getUserMap();
        return null;
    }
    private void checkPaused(){
        if(Logic.isPaused() && !isPaused ) {
            isPaused=true;
            int x=View.getView().pauseMessage();
            if(x==0)
            cancel();
            else if(x==2)
                FeatureAdder.getInstance().loadClass();
        }
        else if(!Logic.isPaused())
        isPaused=false;
    }
    @Override
    public void run() {
        try {
            checkPaused();
            setIO();
        }catch (Throwable e){
            e.printStackTrace();
        }
    }
    private void setIO(){
        logic.read(View.getView().listenInfo());
        Logic.staticUpdateTime();
        logic.updateTime();
        View.getView().setValues(logic.graphicInfo(),true);
    }
}
