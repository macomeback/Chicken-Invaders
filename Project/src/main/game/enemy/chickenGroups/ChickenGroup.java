package main.game.enemy.chickenGroups;
import main.game.MVC.GamePanel;
import main.game.stuff.Kind;
import main.game.stuff.KindPowerUp;
import java.awt.Point;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public abstract class ChickenGroup implements Serializable {
    protected int timeUnit=0;
    private static final long serialVersionUID=1L;
    protected ArrayList<? extends Chicken> chickens;
    private ArrayList<Chicken> removals=new ArrayList<>();
    private ArrayList<Double> powerIndexes=new ArrayList<>();
    private ArrayList<Point> coinPoints=new ArrayList<>(),tempPowerUpPoints=new ArrayList<>(),numberPowerUpPoints=new ArrayList<>(),positions,chickenPositions;
    private ArrayList<KindPowerUp> kindPowerUps=new ArrayList<>(),inKindPowerUps=new ArrayList<>();
    private ArrayList<Egg> eggs;
    protected int width= GamePanel.CHICKEN_WIDTH,height=GamePanel.CHICKEN_HEIGHT;
    private static List<Class<? extends ChickenGroup>> chickenGroups=new ArrayList<>();
    private int grade=0;
    static {
        addDefaultClasses();
       }
    private static void addDefaultClasses(){
        chickenGroups.add(RectangleChickenGroup.class);
        chickenGroups.add(SuicideChickenGroup.class);
        chickenGroups.add(CircleChickenGroup.class);
        chickenGroups.add(SurroundChickenGroup.class);
    }
  ChickenGroup(int level) {
        setChickens(level);
      positions =new ArrayList<>();
      eggs =new ArrayList<>();
      chickenPositions=new ArrayList();
    }
    public static void addNewChickenGroup(Class<? extends ChickenGroup> c){
        chickenGroups.add(c);
    }
    public static ChickenGroup newChickenGroup(int level,int maxPlayers){
        Chicken.setMaxPlayers(maxPlayers);
        int random=(int)(Math.random()*chickenGroups.size());
        try {
           Constructor<? extends ChickenGroup> c=chickenGroups.get(random).getDeclaredConstructor(int.class);
           c.setAccessible(true);
           return c.newInstance(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    protected abstract void setChickens(int level);
    protected abstract void move();
    public void timeUnitPassed(){
        timeUnit++;
        timeUnit%=400;
        if(timeUnit%40==0)
            eggShoot();
        if(timeUnit%4==0)
            move();
        fall(coinPoints);
        fall(numberPowerUpPoints);
        fall(tempPowerUpPoints);
        for(Egg egg:eggs)
            egg.move();
        for(KindPowerUp kindPowerUp:kindPowerUps)
            kindPowerUp.move();
    }
    private void fall(ArrayList<Point> points){
        for(Point point:points)
            if (point.y<1100)
                point.y+=8;
    }
   public Point[] chickensPositions(){
        chickenPositions.clear();
        for(int i=0;i<chickens.size();i++) chickenPositions.add(chickens.get(i).getPoint());
        return chickenPositions.toArray(new Point[chickenPositions.size()]);
    }
    public Point[] eggPositions(){
        positions.clear();
        for(Egg egg:eggs)
            if(egg.getY()<1100)
                positions.add(egg.getPoint());
        return positions.toArray(new Point[positions.size()]);
    }
    public Point[] coinPositions(){
        return positions(coinPoints);
    }
    public Point[] numberPowerUpPositions(){
        return positions(numberPowerUpPoints);
    }
    public Point[] tempPowerUpPositions(){
        return positions(tempPowerUpPoints);
    }
    private Point[] positions(ArrayList<Point> points){
        positions.clear();
        for(Point point:points)
            if(point.y<1100) positions.add(point);
            return positions.toArray(new Point[positions.size()]);
    }
    private void eggShoot(){
       for(Chicken chicken:chickens)
           if(chicken.shoot())
               newEggFall(chicken.x,chicken.y,chicken.getSpeed());
    }
    private void newEggFall(int x,int y,int speed){
        for(Egg egg:eggs)
            if(egg.getY()<1100) {
                egg.initialize(x,y,speed);
                return;
            }
        eggs.add(new Egg(x,y,speed));
    }
    public void collapse(){
       chickens.clear();
    }
    private void newFall(Chicken chicken,ArrayList<Point> points){
      newFall(chicken.x,chicken.y,points);
    }
    private void newFall(int x,int y,ArrayList<Point> points){
        for(Point point:points)
            if(point.y>=1100){
                point.setLocation(x,y);
                return;
            }
        points.add(new Point(x,y));
    }
    private void newKindFall(int x,int y,ArrayList<KindPowerUp> kindPowerUps){
        for(KindPowerUp kindPowerUp:kindPowerUps)
            if(kindPowerUp.getY()>=1100){
                kindPowerUp.initialize(x,y);
                return;
            }
        kindPowerUps.add(new KindPowerUp(x,y));
    }
    public KindPowerUp[] kindPowerUps(){
        inKindPowerUps.clear();
        for(KindPowerUp kindPowerUp:kindPowerUps)
            if(kindPowerUp.getY()<1100) inKindPowerUps.add(kindPowerUp);
            return inKindPowerUps.toArray(new KindPowerUp[inKindPowerUps.size()]);
    }
    public void addIndex(int i,double power){
        removals.add(chickens.get(i));
        powerIndexes.add(power);
    }
    public void addIndex(int i){
        addIndex(i,-1);
    }
    private boolean isOutOfPage(){
        for(Chicken chicken:chickens)
            if(!chicken.isOutOfPage())
                return false;
            return true;
    }
    public void bossPowerUps(){
            int neutron=0,corn=0,utensil=0,number=0,temp=0;
            for(int i=0;i<5;i++) {
                int random=(int)(Math.random()*5);
                switch (random) {
                    case 0:
                        utensil++;
                        break;
                    case 1:
                        corn++;
                        break;
                    case 2:
                        neutron++;
                        break;
                    case 3:
                        number++;
                        break;
                    case 4:
                        temp++;
                        break;
                }
            }
            kindPowerUps.clear();
            int x=200;
            x=addKindPowerUp(Kind.UTENSIL,utensil,x);
            x=addKindPowerUp(Kind.CORN,corn,x);
            x=addKindPowerUp(Kind.NEUTRON,neutron,x);
            x=addPowerUp(number,x,numberPowerUpPoints);
            addPowerUp(number,x,tempPowerUpPoints);
    }
    private int addPowerUp(int number,int x,ArrayList<Point> points){
        for(int i=0;i<number;i++){
            newFall(x,200,points);
            x+=100;
        }
        return x;
    }
    private int addKindPowerUp(Kind kind, int number,int x){
        for(int i=0;i<number;i++) {
            kindPowerUps.add(new KindPowerUp(kind, x, 200));
            x+=100;
        }
        return x;
    }
    public int getGrade(){
        return grade;
    }
    public boolean isDead(){
        return isOutOfPage() || chickens.isEmpty();
    }
    public synchronized boolean collide() {
        boolean isBeforeEmpty=chickens.isEmpty() || isOutOfPage();
        Chicken chicken;
        for (int i=0;i<removals.size() ; i++){
            chicken=removals.get(i);
            if (chicken.shot(powerIndexes.get(i))) {
                if (Math.random() <= .06)
                    newFall(chicken, coinPoints);
                double random = Math.random();
                if (random <= .015)
                    newFall(chicken, numberPowerUpPoints);
                else if (random <= .03)
                    newFall(chicken, tempPowerUpPoints);
                if(Math.random()<=.03)
                    newKindFall(chicken.x,chicken.y,kindPowerUps);
                grade+=chicken.getLevel();
                chickens.remove(chicken);
            }
    }
        removals.clear();
        powerIndexes.clear();
        return isDead() && !isBeforeEmpty;
    }
}
