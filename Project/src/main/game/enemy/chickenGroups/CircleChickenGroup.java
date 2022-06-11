package main.game.enemy.chickenGroups;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

class CircleChickenGroup extends ChickenGroup implements Serializable {
    private static final long serialVersionUID=1L;
    private ArrayList<CircleChicken> circleChickens;
    private Point center,destinationCenter;
    CircleChickenGroup(int level){
        super(level);
        selectDestination();
    }
    private void selectDestination(){
        destinationCenter.setLocation((int) (Math.random() * 600 + 200), (int) (Math.random() * 400 + 50));
    }

    @Override
    public void timeUnitPassed() {
        super.timeUnitPassed();
        centerMove();
        if(timeUnit%200==0) selectDestination();
    }
    private boolean centerMove(){
        double speed=5,y2=destinationCenter.y,x2=destinationCenter.x,x1=center.x,y1=center.y,vx,vy;
        if(Math.sqrt(Math.pow(y2-y1,2)+Math.pow(x2-x1,2))<15)
            return true;
        vx=speed*(x2-x1)/Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
        vy=speed*(y2-y1)/Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
        center.x+=vx;
        center.y+=vy;
        return false;
    }
    @Override
    protected void setChickens(int level) {
        center =new Point(-300,-300);
        destinationCenter=new Point();
        circleChickens=new ArrayList<>();
        chickens=circleChickens;
       for(int i=1;i<4;i++)
            for (int j=0;j<5*i;j++)
            circleChickens.add(new CircleChicken(level,center,50*i,2*Math.PI*j/(5*i)));
            selectDestination();
     }
    @Override
    public void move() {
        for(CircleChicken circleChicken: circleChickens) circleChicken.move();
    }
}
