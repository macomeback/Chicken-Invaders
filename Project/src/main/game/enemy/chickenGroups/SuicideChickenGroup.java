package main.game.enemy.chickenGroups;
import java.awt.Point;
import java.util.ArrayList;
public class SuicideChickenGroup extends ChickenGroup{
    private ArrayList<SuicideChicken> suicideChickens;
    private Point[] planePoints;
    SuicideChickenGroup(int level){
        super(level);
    }
    @Override
    public void timeUnitPassed() {
        super.timeUnitPassed();
        if(timeUnit==0)
            attack();
    }
    public void setPlanePoints(Point[] planePoints){
        this.planePoints=planePoints;
    }
    private void attack(){
        if(!suicideChickens.isEmpty()) {
            int random = (int) (Math.random() * suicideChickens.size());
            suicideChickens.get(random).attack();
        }
    }
    @Override
    protected void setChickens(int level) {
        suicideChickens=new ArrayList<>();
        chickens=suicideChickens;
        for(int i=0;i<10;i++){
            Point point=getRandomPoint();
            suicideChickens.add(new SuicideChicken(level));
        }
    }
   @Override
    protected void move() {
       for(SuicideChicken chicken:suicideChickens) chicken.move();
    }
    private Point getRandomPoint(){
        return new Point((int)(Math.random()*1200)-width,(int)(Math.random()*1000)-height);
    }
    private class SuicideChicken extends Chicken{
        double speed,vx,vy;
        Point destination;
        SuicideChicken(int level){
            super(level,-100,-100);
            selectDestination();
        }
        void selectDestination(){
            if(destination==null) destination=new Point();
            speed=12;
            Point point=getRandomPoint();
            destination.setLocation(point.x,point.y);
            double y2=destination.y,x2=destination.x,x1=x,y1=y;
            vx=getSpeed(x1,x2,y1,y2);
            vy=getSpeed(y1,y2,x1,x2);
        }
        double getSpeed(double x1,double x2,double y1,double y2){
            return speed*(x2-x1)/Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
        }
        void attack(){
            if(planePoints!=null && planePoints.length!=0) {
                int random=(int)(Math.random()*planePoints.length);
                destination.setLocation(planePoints[random]);
                speed *= 2;
            }
        }
        @Override
        protected void move() {
            if(Math.abs(destination.x-x)<width && Math.abs(destination.y-y)<height) selectDestination();
            x+=vx;
            y+=vy;
        }
    }
}
