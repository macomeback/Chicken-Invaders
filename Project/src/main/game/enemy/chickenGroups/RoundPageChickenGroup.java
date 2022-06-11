
package main.game.enemy.chickenGroups;

import java.util.ArrayList;

public class RoundPageChickenGroup extends ChickenGroup {
    private ArrayList<RoundPageChicken> roundPageChickens;
    private int rightX,downY;
    RoundPageChickenGroup(int level){
        super(level);
    }
    @Override
    protected void setChickens(int level) {
        roundPageChickens=new ArrayList<>();
        chickens=roundPageChickens;
       int i=0,x=0,y=0,row=1200/width,column=750/height;
           while(i++<row) {
               roundPageChickens.add(new RoundPageChicken(level, x, y));
               x+=width;
           }
           rightX=x;
           while(i++<row+column) {
               roundPageChickens.add(new RoundPageChicken(level, x, y));
               y+=height;
           }
           downY=y;
           while(i++<40 && i<2*row+column){
               roundPageChickens.add(new RoundPageChicken(level,x,y));
               x-=width;
           }
    }
    @Override
    protected void move() {
        for(RoundPageChicken chicken:roundPageChickens)
            chicken.move();
    }
    private class RoundPageChicken extends Chicken{
        static final int R=1,L=-1,U=2,D=-2;
        int direction;
        RoundPageChicken(int level,int x,int y){
            super(level,x,y);
        }
        @Override
        protected void move() {
            direction=findDirection();
            switch (direction){
                case R:
                    x+=10;
                    break;
                case L:
                    x-=10;
                    break;
                case U:
                    y-=10;
                    break;
                case D:
                    y+=10;
            }
        }
        int findDirection(){
            if(y<=5) {
                if (x>=rightX)
                    return D;
                return R;
            }
            if(y>=downY){
                if(x>0)
                    return L;
                return U;
            }
            if(x<=5)
                return U;
             return D;
        }
    }
}
