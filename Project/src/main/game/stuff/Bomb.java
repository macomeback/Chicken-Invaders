package main.game.stuff;


public class Bomb implements Runnable{
    private double x,y,vx,vy;
    private boolean cancelled=false,called=false;
    public Bomb(int x,int y){
        this.x=x; this.y=y;
        setVxy();
    }
    @Override
    public void run() {
        if (distance()>10) {
            x+=vx;
            y+=vy;
        }
        else
            cancelled = true;
    }
    private double distance(){
       return Math.pow((Math.pow(600-x,2)+Math.pow(400-y,2)),.5);
    }
    private void setVxy(){
        double distance=distance();
        double cos=(600-x)/distance,sin=(400-y)/distance;
        vx=10*cos;
        vy=10*sin;
    }
    public int getX(){
        return (int)x;
    }
    public int getY(){
        return (int)y;
    }
    public boolean isCancelled(){
        return cancelled;
    }
    public boolean shouldExplode(){
       if(cancelled && !called) {
         called=true;
         return true;
       }
        return false;
    }
}