package main.game.enemy.boss;

class UsualBoss extends Boss {
    private boolean[] shouldShoot=new boolean[8];
UsualBoss(int level){
    super(level);
}
    @Override
    protected void shoot() {
        for(int i=0;i<8;i++) {
            shouldShoot[i] = Math.random() <= 0.25;
            if(shouldShoot[i])
                shootRocket(45*i);
        }
    }
    @Override
    protected void move(){
        if(x!=600){
            x+=50;
            y+=50;
        }
    }
}
