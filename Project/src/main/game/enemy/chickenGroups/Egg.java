package main.game.enemy.chickenGroups;

import java.awt.Point;

class Egg {
    private Point point=new Point();
    private int speed;
    Egg(int x,int y,int speed){
        initialize(x,y,speed);
    }
    void initialize(int x,int y,int speed){
        point.x=x; point.y=y;
        this.speed=speed*8;
    }
    void move(){
        if(point.y<1100)
        point.y+=speed;
    }
    int getX(){
        return point.x;
    }
    int getY(){
        return point.y;
    }
    Point getPoint(){
        return point;
    }
}
