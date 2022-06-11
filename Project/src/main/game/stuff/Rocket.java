package main.game.stuff;
import java.awt.Point;
import java.io.Serializable;

public class Rocket implements Serializable {
    private static final long serialVersionUID=1L;
    private double power,addPower,x,y,direction,speed=20;
    private int width,height;
    private Kind kind=Kind.NORMAL;
    private Point point=new Point();
    public Rocket(Kind kind,double direction,int x,int y){
        initialize(kind,direction,x,y);
    }
    public Rocket(Kind kind,double direction,int x,int y,int speed){
        initialize(kind,direction,x,y,speed);
    }
    public void initialize(Kind kind,double direction,int x,int y,int speed){
        this.speed=speed;
        initialize(kind,direction,x,y);
    }
    public void initialize(Kind kind,double direction,int x,int y){
        this.x=x;
        this.y=y;
        this.kind=kind;
        this.direction=Math.toRadians(direction);
        power=kind.getPower();
        width=kind.getWidth();
        height=kind.getHeight();
        addPower=power*0.25;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public double getPower(){
        return power;
    }
    public double getDirection(){
        return direction;
    }
    public void addPower(double number){
        power=power+number*addPower;
    }
    public Kind getKind(){
        return kind;
    }
    public void move(){
        x=x+speed*Math.cos(direction);
        y=y-speed*Math.sin(direction);
    }
    public void out(){
        y=-210;
    }
    public int getX(){
        return (int)x;
    }
    public int getY(){
        return (int)y;
    }
    public Point getPoint() {
        point.setLocation(x,y);
        return point;
    }
}