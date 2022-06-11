package main.game.enemy.chickenGroups;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class SurroundChickenGroup extends ChickenGroup implements Serializable {
    private static long serialVersionUID=1L;
    private Point center;
    private ArrayList<SurroundChicken> surroundChickens;
    SurroundChickenGroup(int level){
        super(level);
    }
    @Override
    protected void setChickens(int level) {
        center=new Point(600,400);
        surroundChickens=new ArrayList<>();
        chickens=surroundChickens;
        for(int i=1;i<4;i++)
            for (int j=0;j<5*i;j++)
                surroundChickens.add(new SurroundChicken(level,center,1000+50*i,50*i+100,2*Math.PI*j/(5*i)));
    }

    @Override
    protected void move() {
        for(SurroundChicken chicken:surroundChickens)
            chicken.move();
    }
    private class SurroundChicken extends CircleChicken implements Serializable{
        double wantedRadius;
        SurroundChicken(int level,Point center,double radius,double wantedRadius,double theta){
            super(level,center,radius,theta);
            this.wantedRadius=wantedRadius;
            angle=Math.PI/40;
        }

        @Override
        protected void move() {
            super.move();
            if(radius-wantedRadius>10)
                radius-=10;
            else
                radius=wantedRadius;
        }
    }
}
