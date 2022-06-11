package main.game.stuff;
import java.awt.*;
import java.io.Serializable;
import java.util.TimerTask;
import java.util.Timer;
public class Plane implements Serializable {
    private static final long serialVersionUID=1L;
    private int x=750,y=600,temp=1,maxTemp=100,hp=5,coins;
    private Point point=new Point();
    private transient boolean heatApex=false;
    private transient Timer timer=new Timer();
    public void initialize(){
        timer=new Timer();
    }
    public void move(int x,int y){
       this.x=x; this.y=y;
    }
    public int getMaxTemp(){
        return maxTemp;
    }
    public void addMaxTemp(){
        maxTemp+=5;
    }
    public int getHp(){
        return hp;
    }
    public int getCoins(){
        return coins;
    }
    public boolean shot(){
        coins=0;
        maxTemp=100;
        x=750;
        y=600;
        return --hp==0;
    }
    public void addCoin(){
        coins++;
    }
    public void nextLevel(){
        coins=0;
        hp=5;
    }
    public void cool(){if(heatApex) return;
        if(temp>40) temp-=40;
        else temp=0;
    }
    public void heat(int heat) {
        if(temp+heat<maxTemp) {temp+=heat;
        shake();
             }
        else {
            temp=maxTemp;
            coolDown();
        }
    }
    private void shake(){
        y+=5;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                y-=5;
            }
        },25);
    }
    public int getTemp(){
        return temp;
    }
    public boolean isHeatApex() {
        return heatApex;
    }
    private void coolDown(){
        if(!heatApex) {
            heatApex=true;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    heatApex = false;
                    temp = 0;
                }
            }, 4000);
        }
    }
    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
    public Point getPoint(){
        point.setLocation(x,y);
        return point;
    }
}
