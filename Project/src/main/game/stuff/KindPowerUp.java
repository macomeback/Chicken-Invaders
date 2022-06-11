package main.game.stuff;

import java.awt.Point;
import java.io.Serializable;

public class KindPowerUp implements Serializable {
   private static final long serialVersionUID=1L;
   private Kind kind;
   private Point point=new Point();
   private int x,y;
   public KindPowerUp(int x,int y){
       initialize(x,y);
   }
   public KindPowerUp(Kind kind,int x,int y){
       this.kind=kind;
       this.x=x; this.y=y;
   }
   public void initialize(int x,int y){
       this.x=x; this.y=y;
       double random=Math.random()*3;
       if(random<1)
           kind=Kind.UTENSIL;
       else if(random<2)
           kind=Kind.CORN;
       else
           kind=Kind.NEUTRON;
   }
   public void out(){
       y=1110;
   }
   public void move(){
       if(y<1100)
       y+=8;
   }
   public int getX(){
       return x;
   }
   public int getY(){
       return y;
   }
   public Point getPoint(){
       point.setLocation(x,y);
       return point;
   }
   public Kind getKind(){
       return kind;
   }
}
