package main.game.stuff;

import main.game.MVC.GamePanel;

public enum Kind {
    NORMAL(0),UTENSIL(1),CORN(2),NEUTRON(3);
    private double power;
    private int heat,width,height,timeInterval;
    Kind(int kind){
       switch (kind){
           case 0:
               width= GamePanel.MISSILE_WIDTH;
               height=GamePanel.MISSILE_HEIGHT;
               power=1;
               timeInterval=8;
               heat=5;
               break;
           case 1:
               width=GamePanel.UTENSIL_WIDTH;
               height=GamePanel.MISSILE_HEIGHT;
               power=3;
               timeInterval=10;
               heat=10;
               break;
           case 2:
               width=GamePanel.CORN_WIDTH;
               height=GamePanel.CORN_HEIGHT;
               power=1.5;
               timeInterval=5;
               heat=5;
               break;
           case 3:
               width=GamePanel.NEUTRON_WIDTH;
               height=GamePanel.NEUTRON_HEIGHT;
               power=2;
               timeInterval=12;
               heat=8;
               break;
       }
    }
       int getWidth(){
        return width;
    }
    int getHeight(){
        return height;
    }
    public int getHeat(){
        return heat;
    }
    double getPower(){
        return power;
    }
    public int getTimeInterval(){
        return timeInterval;
    }
}
