package main.game.MVC;
import main.game.enemy.boss.Boss;
import main.game.enemy.chickenGroups.SuicideChickenGroup;
import main.game.enemy.chickenGroups.SurroundChickenGroup;
import main.game.stuff.*;
import main.game.enemy.chickenGroups.ChickenGroup;
import java.awt.Point;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class Logic {
    private static int bossShoot,coinWidth,coinHeight,chickenWidth,chickenHeight,eggWidth,eggHeight,planeWidth,planeHeight,numberPowerUpWidth,numberPowerUpHeight, giftWidth, giftHeight;
    private static Map<String,Point> planePoints=new HashMap<>();
    private static List<Logic> logics=new ArrayList<>();
    private static List<Rocket> allRockets=new ArrayList<>();
    private static List<Point> allBombPositions=new ArrayList<>();
    private static ChickenGroup chickenGroup;
    private int maxLevel=4,second,grade,wave,numberPowerUps,timeUnit,deActiveTime,startShoot,level,bombs;
    private String name;
    private long secondsPlayed;
    private double addPower=0;
    private String message;
    private Map<String,Serializable> graphicInfo=new HashMap<>();
    private Plane plane;
    private Bomb[] bomb;
    private Runnable cool;
    private Map<String,?> userMap;
    private static boolean isPaused=true,warning=false;
    private boolean paused,shootBomb=false,shouldShoot=false,afterApex=false;
    private static boolean bossTime=false;
    private ScheduledExecutorService scheduler =Executors.newScheduledThreadPool(1);;
    private static Point[] chickensLocations,eggPoints,coinPoints,numberPowerUpPoints,tempPowerUpPoints;
    private static KindPowerUp[] kindPowerUps;
    private Kind kind;
    private List<Rocket> rockets=new ArrayList<>();
    private static List<Rocket> bossRockets=new ArrayList<>();
    private static Boss boss;
     //network or single?
    static Logic newLogic(Map<String,?> userMap,String name,int maxLevel){
        Logic logic=new Logic(userMap,name,maxLevel);
        logics.add(logic);
        return logic;
    }
    public static Logic newLogic(String name,int maxLevel){
        return newLogic(null,name,maxLevel);
    }
    private Logic(Map<String,?> userMap,String name,int maxLevel){
        initialize(userMap,name,maxLevel);
    }
    private void initialize(Map<String,?> userMap,String name,int maxLevel){
        this.name=name;
        this.maxLevel=maxLevel;
        this.userMap=userMap;
        loadValues();
        cool();
    }
    public static void remove(Logic logic){
        logics.remove(logic);
    }
    static void clear(){
        logics.clear();
    }
    private static void setUserRelatedGraphics(){
        planePoints.clear();
        allRockets.clear();
        allBombPositions.clear();
            for (Logic logic : logics) {
                allRockets.addAll(logic.rockets);
                allBombPositions.addAll(logic.bombPositions());
                if (logic.deActiveTime == 0)
                    planePoints.put(logic.name, logic.plane.getPoint());
            }
    }
    private void resetValues(){
        bomb=new Bomb[bombs];
        rockets.clear();
        timeUnit=0;
        deActiveTime=0;
        startShoot=0;
        shouldShoot=false;
            newWave(++wave);
        if(bossTime)
            boss=Boss.newBoss(level);
        plane.move(750,600);
        isPaused=false;
    }
    private static boolean checkPaused(){
        for(Logic logic:logics)
            if(logic.paused)
                return true;
            return false;
    }
    public static boolean isPaused(){
        return isPaused;
    }
    private static void checkPlanesShootingOthers(){
    for(Iterator<Logic> i=logics.iterator();i.hasNext();){
        Logic l1=i.next();
        Iterator<Logic> j=logics.iterator();
        while(j.hasNext()){
           Logic l2=j.next();
           if(l1!=l2 && l1.deActiveTime==0 && isRocketPlaneCollision(l1.plane.getX(),l1.plane.getY(),l2.rockets))
               l1.planeShot();
        }
    }
    }
//    first plane x,y then rocket
    private static boolean isRocketPlaneCollision(int x,int y,List<Rocket> rockets){
        for(Rocket r:rockets)
        if(r.getX()+r.getWidth()>x && r.getX()<x+planeWidth && r.getY()<y+planeHeight && r.getY()+r.getHeight()>y)
            return true;
        return false;
    }
    public static void staticUpdateTime(){
        isPaused=checkPaused();
        if(!isPaused){
            setUserRelatedGraphics();
            if(bossTime)
                handleBoss();
            else
                handleChickenGroup();
            checkPlanesShootingOthers();
        }
    }
    public void updateTime(){
            if (!isPaused) {
                timeUnitPassed();
                handleRockets();
                if (bossTime)
                    checkBossCollision();
                else
                    checkCollision();
                handleBombs();
                if (timeUnit == 0)
                    cool.run();
                if (deActiveTime > 0 && --deActiveTime==0)
                        plane.move(750,600);
            }
    }
    private void timeUnitPassed(){
        if(timeUnit==0)
            secondsPlayed++;
        timeUnit++;
        timeUnit %= 40;
    }
    private boolean canShoot(){
       return deActiveTime == 0 && shouldShoot && !plane.isHeatApex();
    }
    private void handleRockets(){
        if (canShoot())
            shootRockets();
        for (Rocket rocket : rockets)
            if (rocket.getY() > -200)
                rocket.move();
    }
    private static void handleChickenGroup(){
        if(chickenGroup!=null) {
            chickenGroup.timeUnitPassed();
            setChickenFallenThings();
            if(chickenGroup instanceof SuicideChickenGroup)
                ((SuicideChickenGroup) chickenGroup).setPlanePoints(planePoints.values().toArray(new Point[planePoints.size()]));
        }
    }
    private static void setChickenFallenThings(){
        chickensLocations = chickenGroup.chickensPositions();
        eggPoints = chickenGroup.eggPositions();
        coinPoints = chickenGroup.coinPositions();
        numberPowerUpPoints = chickenGroup.numberPowerUpPositions();
        tempPowerUpPoints = chickenGroup.tempPowerUpPositions();
        kindPowerUps = chickenGroup.kindPowerUps();
    }
    private static void handleBoss(){
        bossTimePass();
        bossRockets=boss.getRockets();
        for(Rocket rocket:bossRockets)
            rocket.move();
    }
    private void handleBombs(){
        if (bomb != null)
            for (Bomb bomb1 : bomb)
                if (bomb1 != null) {
                    bomb1.run();
                    if (bomb1.shouldExplode()) {
                        if(bossTime)
                            bossShot(50);
                        else
                        {
                            chickenGroup.collapse();
                            newWave(++wave);
                        }
                        bomb1 = null;
                    }
                }
    }
    public void read(Map<String,?> listenerInfo){
        paused=(Boolean)listenerInfo.get("paused");
        if(isPaused)
            return;
            if(!shootBomb && (Boolean)listenerInfo.get("shootBomb"))
                shootBomb();
            shootBomb=(Boolean)listenerInfo.get("shootBomb");
            shouldShoot=(Boolean)listenerInfo.get("shouldShoot");
            plane.move((Integer)listenerInfo.get("mouseX"),(Integer)listenerInfo.get("mouseY"));
    }
    public Map<String,Serializable> graphicInfo(){
        Map<String,Point> planes=Collections.synchronizedMap(planePoints);
        graphicInfo.put("planePoints",(Serializable) planes);
            graphicInfo.put("message",message);
            graphicInfo.put("temp",plane.getTemp());
            graphicInfo.put("maxTemp",plane.getMaxTemp());
            graphicInfo.put("second",Integer.toString(second));
            graphicInfo.put("warning",warning);
            graphicInfo.put("bombs",bombs);
            graphicInfo.put("hp",plane.getHp());
            graphicInfo.put("coins",plane.getCoins());
            graphicInfo.put("grade",getGrade());
            graphicInfo.put("bossTime",bossTime);
            if(bossTime){
                graphicInfo.put("bossX",boss.getX());
                graphicInfo.put("bossY",boss.getY());
                graphicInfo.put("bossRockets",bossRockets.toArray(new Rocket[0]));
            }
            else {
                graphicInfo.put("chickensLocations", chickensLocations);
                graphicInfo.put("coinPoints", coinPoints);
                graphicInfo.put("eggPoints", eggPoints);
            }
        graphicInfo.put("rockets",allRockets.toArray(new Rocket[0]));
        graphicInfo.put("bombPositions",allBombPositions.toArray(new Point[0]));
        graphicInfo.put("numberPowerUpPoints",numberPowerUpPoints);
        graphicInfo.put("tempPowerUpPoints",tempPowerUpPoints);
        graphicInfo.put("kindPowerUps",kindPowerUps);
        graphicInfo.put("playSound",startShoot==0 && canShoot());
        return graphicInfo;
    }
    private static void bossTimePass(){
        if(bossShoot==0)
            boss.timeUnitPassed();
        bossShoot++;
        bossShoot%=20;
    }
    private void checkBossCollision(){
        for(Rocket rocket:rockets) {
            double x=rocket.getX(),y=rocket.getY();
            if (Math.pow((x-boss.getX())/143,2)+Math.pow((y-boss.getY())/183,2)<1) {
                bossShot(rocket.getPower());
                rocket.out();
            }
        }
        if (Math.pow(((double)plane.getX()-boss.getX())/143,2)+Math.pow(((double)plane.getY()-boss.getY())/183,2)<1){
            bossShot(20);
            planeShot();
            for (Rocket rocket : bossRockets)
                planeShot(rocket.getPoint(), rocket.getWidth(), rocket.getHeight());
        }
    }
    private void bossShot(double power){
        if(boss.shot(power))
            nextLevel();
    }
    private void newWave(int newWave,boolean mayBoss){
        wave=newWave;
        if(wave!=1 && wave%maxLevel==1 && mayBoss) {
            boss = Boss.newBoss(level);
            bossTime=true;
        }
        else {
            message="Wave: " + wave;
            scheduler.schedule(()->{
                    message="";
                    int previousGrade=0;
                    if(chickenGroup!=null)
                    previousGrade=chickenGroup.getGrade();
                    newChickenGroup();
                    grade+=previousGrade;
            }, 3, TimeUnit.SECONDS);
        }
    }
    private void newWave(int newWave){
        newWave(newWave,true);
    }
    private void nextLevel(){
        bombs++;
        bomb=new Bomb[bombs];
        chickenGroup.bossPowerUps();
        bossTime=false;
        if(++level>4)
            View.getView().show(State.RANK);
        else {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    message="Level: " + level;
                }
            }, 4, TimeUnit.SECONDS);
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    grade=grade+3*plane.getCoins();
                    plane.nextLevel();
                    newWave(wave,false);
                }
            }, 8, TimeUnit.SECONDS);
        }
    }
    private void newChickenGroup(){
        if(chickenGroup!=null && !chickenGroup.isDead()) return;
        chickenGroup=ChickenGroup.newChickenGroup(level,logics.size());
        if(chickenGroup instanceof SurroundChickenGroup) {
            warning=true;
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    warning=false;
                }
            },3,TimeUnit.SECONDS);
        }
    }
    private void checkCollision(){
        if(chickenGroup==null) return;
        checkEggCollision();
        checkCoinCollision();
        checkNumberPowerUpsCollision();
        checkTempPowerUpsCollision();
        checkKindPowerUpsCollision();
        checkChickenCollision();
    }
    private void checkEggCollision(){
        for (Point point:eggPoints )
            if(planeShot(point,eggWidth,eggHeight))
                point.y=1150;
    }
    private void checkCoinCollision(){
        for(Point point:coinPoints)
            if (isPlaneCollision(point, coinWidth, coinHeight)) {
                plane.addCoin();
                point.y = 1150;
            }
            else if(isUpCollision(point,coinWidth,coinHeight))
                point.y=1150;
    }
    private void checkNumberPowerUpsCollision(){
        for(Point point:numberPowerUpPoints)
            if (isPlaneCollision(point, numberPowerUpWidth,numberPowerUpHeight)) {
                numberPowerUps++;
                point.y = 1150;
            }
    }
    private void checkTempPowerUpsCollision(){
        for(Point point:tempPowerUpPoints)
            if (isPlaneCollision(point, giftWidth, giftHeight)) {
                plane.addMaxTemp();
                point.y = 1150;
            }
    }
    private void checkKindPowerUpsCollision(){
        for(KindPowerUp kindPowerUp:kindPowerUps)
            if(isPlaneCollision(kindPowerUp.getPoint(),giftWidth,giftHeight)){
                kind=kindPowerUp.getKind();
                kindPowerUp.out();
            }
    }
    private void checkChickenCollision(){
        for (int i = 0; i < chickensLocations.length; i++)
            if (planeShot(chickensLocations[i], chickenWidth, chickenHeight))
                chickenGroup.addIndex(i);
            else {
                double power = isChickenCollision(chickensLocations[i],chickenWidth,chickenHeight);
                if (power != 0)
                    chickenGroup.addIndex(i, power);
            }
        if (chickenGroup.collide())
            newWave(++wave);
    }
    private boolean planeShot(Point point,int width,int height){
        if (isPlaneCollision(point, width, height)) {
            planeShot();
            return true;
        }
        return false;
    }
    private void planeShot(){
        kind=Kind.NORMAL;
        numberPowerUps=0;
        deActiveTime=200;
        if(plane.shot())
           View.getView().show(State.RANK);
    }
    private void shootRockets(){
        if(startShoot==0) {
            plane.heat(kind.getHeat());
            tempChange();
            switch (numberPowerUps) {
                case 0:
                    shootRocket(90, plane.getX() + planeWidth / 2 - 25, plane.getY());
                    break;
                case 1:
                    shootRocket(90, plane.getX() + planeWidth / 2 - 30, plane.getY());
                    shootRocket(90, plane.getX() + planeWidth / 2, plane.getY());
                    break;
                case 2:
                    shootRocket(90, plane.getX() + planeWidth / 2 - 25, plane.getY());
                    shootRocket(85, plane.getX() + planeWidth / 2 - 25, plane.getY());
                    shootRocket(95, plane.getX() + planeWidth / 2 - 25, plane.getY());
                    break;
                default:
                    addPower = numberPowerUps - 3;
                    shootRocket(90, plane.getX() + planeWidth / 2 - 15, plane.getY());
                    shootRocket(90, plane.getX() + planeWidth / 2, plane.getY());
                    shootRocket(100, plane.getX() + planeWidth / 2 - 15, plane.getY());
                    shootRocket(80, plane.getX() + planeWidth / 2, plane.getY());
            }
        }
        startShoot++;
        startShoot%=kind.getTimeInterval();
    }
    private void shootRocket(int direction,int x,int y){
            for (Rocket rocket: rockets)
                if (rocket.getY() < -200) {
                    rocket.initialize(kind,direction,x,y);
                    rocket.addPower(addPower);
                    return;
                }
            rockets.add(new Rocket(kind,direction,x, y));
                rockets.get(rockets.size()-1).addPower(addPower);
    }
    private void tempChange(){
        if(!plane.isHeatApex() || !afterApex)
            afterApex=plane.isHeatApex();
    }
    private void shootBomb(){
        if(bombs!=0 && !isPaused && deActiveTime==0) {
            int j = 0;
            for (int i = 0; i < bomb.length; i++)
                if (bomb[i] == null) {
                    j = i;
                    break;
                }
            bomb[j] = new Bomb(plane.getX() + planeWidth/ 2 - 25, plane.getY());
            bombs--;
        }
    }
    private void cool(){
        if(cool==null)
        cool=new Runnable() {
            private int second=4;
            @Override
            public void run() {
                if(plane.isHeatApex()) {
                    shouldShoot=false;
                    Logic.this.second=second;
                    second--;
                    if(second==0) second=4;
                }
                if(!shouldShoot)   {
                    plane.cool();
                    tempChange();
                }
            }};
    }
    private boolean isPlaneCollision(Point point,int width,int height){
        return deActiveTime==0 && point.x + width> plane.getX() && point.x < plane.getX()+ planeWidth && point.y+height>= plane.getY() && point.y<plane.getY()+planeHeight;
    }
    private boolean isUpCollision(Point point,int width,int height) {
       return isChickenCollision(point,width,height)!=0;
    }
    private double isChickenCollision(Point point,int width,int height){
        for (Rocket rocket:rockets)
            if (rocket.getY()>=-140 && rocket.getX() + rocket.getWidth() > point.x && rocket.getX() < point.x + width && rocket.getY() < point.y + height && rocket.getY() + rocket.getHeight() > point.y) {
                rocket.out();
                return rocket.getPower();
            }
        return 0;
    }
    static void setWidthHeight(){
        chickenWidth=GamePanel.CHICKEN_WIDTH;
        chickenHeight=GamePanel.CHICKEN_HEIGHT;
        eggWidth=GamePanel.EGG_WIDTH;
        eggHeight=GamePanel.EGG_HEIGHT;
        coinWidth=GamePanel.COIN_WIDTH;
        coinHeight=GamePanel.COIN_HEIGHT;
        planeWidth=GamePanel.PLANE_WIDTH;
        planeHeight=GamePanel.PLANE_HEIGHT;
        numberPowerUpWidth=GamePanel.NUMBER_GIFT_WIDTH;
        numberPowerUpHeight=GamePanel.NUMBER_GIFT_HEIGHT;
        giftWidth=GamePanel.GIFT_WIDTH;
        giftHeight=GamePanel.GIFT_HEIGHT;
    }
    private void loadValues(){
        if(userMap==null){
            addPower=0;
            secondsPlayed =0;
            wave=0;
            grade=0;
            afterApex=false;
            numberPowerUps=0;
            level=1;
            bombs=3;
            plane= new Plane();
            kind=Kind.NORMAL;
        }
        else{
            plane=(Plane)userMap.get("Plane");
            level = (Integer) userMap.get("level");
            if(plane.getHp()==0 || level>maxLevel) {
                userMap=null;
                loadValues();
                return;
            }
                plane.initialize();
                addPower = (Double) userMap.get("addPower");
                secondsPlayed = (Long) userMap.get("secondsPlayed");
                wave = (Integer) userMap.get("wave");
                grade = (Integer) userMap.get("grade");
                afterApex = (Boolean) userMap.get("afterApex");
                numberPowerUps = (Integer) userMap.get("numberPowerUps");
                bombs = (Integer) userMap.get("bombs");
                kind = (Kind) userMap.get("Kind");
        }
        resetValues();
    }
    Map<String,?> getUserMap(){
        Map userMap=new HashMap();
        userMap.put("addPower",addPower);
        userMap.put("secondsPlayed", secondsPlayed);
        userMap.put("wave",--wave);
        userMap.put("grade",grade);
        userMap.put("bossTime",bossTime);
        userMap.put("afterApex",afterApex);
        userMap.put("numberPowerUps",numberPowerUps);
        userMap.put("level",level);
        userMap.put("bombs",bombs);
        userMap.put("Plane",plane);
        userMap.put("Kind",kind);
        return userMap;
    }
    public int getGrade(){
        if(chickenGroup!=null)
        return grade+chickenGroup.getGrade();
        return 0;
    }
    public int getWave(){
        return wave;
    }
    private List<Point> bombPositions(){
        List<Point> bombPositions=new ArrayList();
        for(Bomb bomb1:bomb)
            if(bomb1!=null && !bomb1.isCancelled())
                bombPositions.add(new Point(bomb1.getX(),bomb1.getY()));
            return bombPositions;
    }
}

