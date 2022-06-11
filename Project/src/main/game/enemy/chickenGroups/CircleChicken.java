package main.game.enemy.chickenGroups;

import java.awt.Point;
import java.io.Serializable;

class CircleChicken extends Chicken implements Serializable {
    private double theta;
    double radius,angle=Math.PI/80;
    private Point center;
    CircleChicken(int level, Point center, double radius, double theta){
        super(level, center.x + (int)(radius*Math.cos(theta)), center.y + (int)(radius*Math.sin(theta)));
        this.theta=theta;
        this.radius=radius;
        this.center=center;
    }
    @Override
    protected void move() {
        theta-=angle;
        if(theta<-2*Math.PI) theta+=Math.PI*2;
        x= center.x + (int)(radius*Math.cos(theta));
        y= center.y + (int)(radius*Math.sin(theta));
    }
}
