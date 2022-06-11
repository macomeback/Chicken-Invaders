package main.game.enemy.chickenGroups;

import java.io.Serializable;
import java.util.ArrayList;

public class RectangleChickenGroup extends ChickenGroup{
    private boolean isMovingRight=true;
    private ArrayList<RectangleChicken> rectangleChickens;
    RectangleChickenGroup(int level){
       super(level);
    }
    @Override
    protected void move(){
        mustChangeDirection();
        for(RectangleChicken chicken:rectangleChickens) chicken.move();
    }
    private void mustChangeDirection(){
        for(RectangleChicken chicken: rectangleChickens) switch (chicken.mustChangeDirection()){
            case 1: isMovingRight=true; return;
            case -1: isMovingRight=false; return;
        }
    }
    @Override
    protected void setChickens(int level){
        int x=-700,y=50;
       rectangleChickens=new ArrayList<>();
        chickens=rectangleChickens;
        for(int i=0;i<3;i++) {
            for (int j = 0; j < 10; j++) {
                rectangleChickens.add(new RectangleChicken(level, x, y));
                x += width;
            }
            x=-700;
            y+=height;
        }
    }
    private class RectangleChicken extends Chicken  {
        RectangleChicken(int level,int x,int y){
            super(level,x,y);
        }
        int mustChangeDirection(){
            if(x+width>1180) return -1;
            else if(x<20) return 1;
            return 0;
        }
        @Override
        protected void move(){
            if(isMovingRight) x+=20;
            else x-=20;
        }
    }
}
