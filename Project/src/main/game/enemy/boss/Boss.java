package main.game.enemy.boss;

import main.game.stuff.Kind;
import main.game.stuff.Rocket;

import java.awt.Point;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class Boss {
    private double hp;
    private ArrayList<Rocket> rockets=new ArrayList<>(),workingRockets=new ArrayList<>();
    private static List<Class<? extends Boss>> bosses=new ArrayList<>();
    protected int x=-150,y=-550;
    static {
        bosses.add(UsualBoss.class);
    }
    protected Boss(int level){
        initialize(level);
    }
    private void initialize(int level){
        hp=250*level;
        x=-150;
        y=-550;
    }
    public static void addNewBoss(Class<? extends Boss> c){
        bosses.add(c);
    }
    public static Boss newBoss(int level){
        int random=(int)(Math.random()*bosses.size());
        try {
            Constructor<? extends Boss> c=bosses.get(random).getDeclaredConstructor(int.class);
            return c.newInstance(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void timeUnitPassed(){
        move();
        shoot();
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
   protected abstract void move();
    protected abstract void shoot();
    public ArrayList<Rocket> getRockets(){
        workingRockets.clear();
        for(Rocket rocket:rockets)
            if(!isOutOfPage(rocket.getPoint()))
                workingRockets.add(rocket);
            return workingRockets;
    }
    public boolean shot(double power){
        hp-=power;
        return hp<=0;
    }
    protected void shootRocket(double direction){
        for(Rocket rocket:rockets)
            if(isOutOfPage(rocket.getPoint())) {
                rocket.initialize(Kind.NORMAL, direction, x , y,5);
                return;
            }
        rockets.add(new Rocket(Kind.NORMAL,direction,x,y,5));
    }
    private boolean isOutOfPage(Point point){
        return point.x<=-150 || point.y<=-150 || point.y>=1150 || point.x>=1300;
    }
}
