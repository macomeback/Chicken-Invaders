package main.game.enemy.chickenGroups;
import main.game.MVC.GamePanel;

import java.awt.Point;
abstract class Chicken {
    private double probability=.05,hp;
    private int eggSpeed,level;
    protected int x,y;
    protected static int width= GamePanel.CHICKEN_WIDTH,maxPlayers=1;
   Chicken(int level,int x,int y){
        this.x=x; this.y=y;
        int power=(int)(Math.random()*(double)level)+1;
        this.level=power;
        switch (power){
            case 1: hp=2;
            case 2: hp=3;
            case 3:
                hp=5;
                probability=0.1;
                break;
            case 4:
                hp=8;
                probability=0.2;
                break;
                default:
                    double pow=(double)power;
                    probability=(pow-2)*.1;
                    hp=8+(pow-4)*3;
        }
       if(power%2==0)
           eggSpeed=power/2;
       else
           eggSpeed=power/2+1;
        hp*=maxPlayers;
       }
       static void setMaxPlayers(double maxPlayers){
       Chicken.maxPlayers=(int)(Math.sqrt(maxPlayers));
       }
    int getLevel(){
        return level;
    }
    int getSpeed(){
        return eggSpeed;
    }
    boolean shoot(){
        return Math.random()<=probability;
    }
    boolean isOutOfPage(){
        return x>=1200 || y>=1000 || y<=-200 || x<=-200;
    }
   boolean shot(double power){
        hp-=power;
        return power==-1 || hp<=0;
    }
   Point getPoint(){
       return new Point(x,y);
   }
   protected abstract void move();
}
