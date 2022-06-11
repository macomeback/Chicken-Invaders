package main.game.MVC;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import main.game.stuff.Kind;
import main.game.stuff.KindPowerUp;
import main.game.stuff.Rocket;

public class GamePanel extends JPanel  {
    public static int CHICKEN_WIDTH,CHICKEN_HEIGHT,EGG_WIDTH,EGG_HEIGHT,COIN_WIDTH,COIN_HEIGHT,PLANE_WIDTH,PLANE_HEIGHT,NUMBER_GIFT_WIDTH,NUMBER_GIFT_HEIGHT,GIFT_WIDTH, GIFT_HEIGHT,MISSILE_WIDTH,MISSILE_HEIGHT,UTENSIL_WIDTH,UTENSIL_HEIGHT,CORN_WIDTH,CORN_HEIGHT,NEUTRON_WIDTH,NEUTRON_HEIGHT;
    private static String defaultPath="./resource/images/";
    private static BufferedImage rocketImg,neutronImg,cornImg,utensilImg,neutronGiftImg,cornGiftImg,utensilGiftImg,bossImg,planeImg,missileImg,backgroundImg,bombImg,coinImg,hpImg,chickenImg,eggImg,numberPowerUpImg,tempPowerUpImg,giftImg;
    private JProgressBar tempBar=new JProgressBar(0,100);
    private JLabel bombLabel,coinLabel,hpLabel,gradeLabel;
    private boolean shouldShoot,shootBomb, paused;
    private Map<String,Serializable> listenInfo=new HashMap<>();
    private Map<String,Point> planePoints,copyPlanePoints=new HashMap<>();
    private int border=0,bombs,coins,hp,grade,temp,maxTemp,mouseX,mouseY,bossX,bossY;
    private Point[] chickensLocations,eggPoints,coinPoints,numberPowerUpPoints,tempPowerUpPoints,bombPoints;
    private Rocket[] bossRockets,rockets;
    private KindPowerUp[] kindPowerUps;
    private boolean warning=false,bossTime=false;
    private String message="";
    private Font font=new Font("San Fransisco",Font.BOLD,20);
    static {
        loadAllImages();
        setWidthHeight();
    }
    GamePanel()  {
        super();
        initiateListeners();
        setLayout(new BorderLayout());
        initiateLabels();
        initiateBar();
    }
    //clean 1 s repaint 100 ms cool 1 s
    private void initiateLabels(){
        bombLabel=new JLabel(new ImageIcon(bombImg));
        coinLabel=new JLabel(new ImageIcon(coinImg));
        hpLabel=new JLabel(new ImageIcon(hpImg));
        gradeLabel=new JLabel();
        setLabel(bombLabel);
        setLabel(coinLabel);
        setLabel(hpLabel);
        setLabel(gradeLabel,false);
        updateValues();
        Box box=Box.createHorizontalBox();
        add(box,BorderLayout.SOUTH);
        box.add(bombLabel);
        box.add(coinLabel);
        box.add(hpLabel);
        box.add(Box.createRigidArea(new Dimension(700,50)));
    }
    private void setLabel(JLabel label,boolean hasColor){
        label.setFont(new Font("San Fransisco",Font.PLAIN,14));
        label.setForeground(Color.WHITE);
        label.setOpaque(hasColor);
        if(hasColor)
        label.setBackground(Color.BLUE);
        label.setMaximumSize(new Dimension(100,50));
    }
    private void setLabel(JLabel label){
        setLabel(label,true);
    }
    private void updateValues(){
        bombLabel.setText(Integer.toString(bombs));
        coinLabel.setText(Integer.toString(coins));
        hpLabel.setText(Integer.toString(hp));
        gradeLabel.setText(Integer.toString(grade));
    }
    private void initiateListeners(){
        MyMouseListener mouseListener=new MyMouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        setFocusable(true);
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
                    paused = true;
            }
        });
    }
    int pauseMessage(){
        String[] st={"Exit","Continue","Add attribute"};
        int x=JOptionPane.showOptionDialog(null,"","",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,st,st[1]);
        if(x==0)
            View.getView().show(State.UI);
        else if(x==2) {
            File classFile=getFeatureInfo();
            String classFullName=JOptionPane.showInputDialog("Enter full class name: ");
            FeatureAdder.getInstance().setArguments(classFile,classFullName);
        }
            paused = false;
        return x;
    }
    private File getFeatureInfo(){
        JFileChooser jfc=new JFileChooser();
        try {
            EventQueue.invokeAndWait(()->{
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jfc.setAcceptAllFileFilterUsed(false);
                jfc.showOpenDialog(null);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return jfc.getSelectedFile();
    }
    private static void loadAllImages(){
        try{
            backgroundImg=loadImage("background");
            planeImg = loadImage("plane");
            missileImg=loadImage("missile");
        bombImg=loadImage("bomb");
        coinImg=loadImage("coin");
        hpImg=loadImage("hp");
        chickenImg=loadImage("chicken");
        eggImg=loadImage("egg");
        numberPowerUpImg=loadImage("power");
        tempPowerUpImg =loadImage("tempGift");
        bossImg=loadImage("boss");
        neutronImg=loadImage("NeutronGun");
        neutronGiftImg=loadImage("GiftNeutronGun");
        cornImg=loadImage("CornShotgun");
        cornGiftImg=loadImage("GiftCornShotGun");
        utensilImg=loadImage("Utensil");
        utensilGiftImg=loadImage("GiftUtensil");
        }
        catch(IOException e){e.printStackTrace();}
    }
    private static BufferedImage loadImage(String imageName) throws IOException {
        return ImageIO.read(new File(defaultPath+imageName+".png"));
    }
    private void tempChange(){
        tempBar.setValue(temp);
        tempBar.setString(Integer.toString(temp));
    }
    private void initiateBar(){
        tempBar.setStringPainted(true);
      tempBar.setValue(temp);
      tempBar.setForeground(Color.GREEN);
      Box box=Box.createHorizontalBox();
      add(box,BorderLayout.NORTH);
        box.add(gradeLabel,BorderLayout.NORTH);
       box.add(tempBar,BorderLayout.NORTH);
       box.add(Box.createRigidArea(new Dimension(900,50)),BorderLayout.NORTH);
    }
    private void moveBackground(){
        border+=5;
       border%=getHeight();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg,0,border,getWidth(),getHeight(),null);
        g.drawImage(backgroundImg,0,border-getHeight(),getWidth(),getHeight(),null);
        moveBackground();
        drawPlanes(g);
        drawObjects(numberPowerUpPoints,numberPowerUpImg,g);
        drawObjects(tempPowerUpPoints, tempPowerUpImg,g);
        drawKindPowerUps(kindPowerUps,g);
        drawRockets(rockets,g);
        drawObjects(bombPoints,bombImg,g);
        if(warning) {
            g.setColor(Color.RED);
            g.drawOval(600, 350, planeImg.getWidth(), planeImg.getHeight());
        }
        if(bossTime){
            drawRockets(bossRockets,g);
            g.drawImage(bossImg,bossX-143,bossY-183,this);
        }
        else {
            drawObjects(chickensLocations, chickenImg, g);
            drawObjects(eggPoints,eggImg,g);
            drawObjects(coinPoints,coinImg,g);
        }
        drawMessage(g);
        updateValues();
    }
    private void drawPlanes(Graphics g){
        if(planePoints!=null) {
            copyPlanePoints.clear();
            copyPlanePoints.putAll(planePoints);
            for (Map.Entry<String, Point> plane : copyPlanePoints.entrySet()) {
                Point point = plane.getValue();
                g.drawImage(planeImg, point.x, point.y, this);
                g.drawString(plane.getKey(), point.x, point.y);
            }
        }
    }
    private void drawKindPowerUps(KindPowerUp[] kindPowerUps,Graphics g){
        if(kindPowerUps!=null)
        for(KindPowerUp kindPowerUp:kindPowerUps) {
            giftImg=giftImg(kindPowerUp.getKind());
            g.drawImage(giftImg,kindPowerUp.getX(),kindPowerUp.getY(),this);
        }
    }
    private void drawMessage(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(message,600,350);
    }
    private static void setWidthHeight(){
        CHICKEN_WIDTH=chickenImg.getWidth();
        CHICKEN_HEIGHT=chickenImg.getHeight();
        EGG_WIDTH=eggImg.getWidth();
        EGG_HEIGHT=eggImg.getHeight();
        COIN_WIDTH=coinImg.getWidth();
        COIN_HEIGHT=coinImg.getHeight();
        PLANE_WIDTH=planeImg.getWidth();
        PLANE_HEIGHT=planeImg.getHeight();;
        NUMBER_GIFT_WIDTH=numberPowerUpImg.getWidth();
        NUMBER_GIFT_HEIGHT=numberPowerUpImg.getHeight();
        GIFT_WIDTH = tempPowerUpImg.getWidth();
        GIFT_HEIGHT = tempPowerUpImg.getHeight();
        MISSILE_WIDTH=missileImg.getWidth();
        MISSILE_HEIGHT=missileImg.getHeight();
        UTENSIL_WIDTH=utensilImg.getWidth();
        UTENSIL_HEIGHT=utensilImg.getHeight();
        CORN_WIDTH=cornImg.getWidth();
        CORN_HEIGHT=cornImg.getHeight();
        NEUTRON_WIDTH=neutronImg.getWidth();
        NEUTRON_HEIGHT=neutronImg.getHeight();
    }
    private void drawRockets(Rocket[] rockets,Graphics g){
        Graphics2D g2d=(Graphics2D)g;
        int x,y;
        AffineTransform tx;
        AffineTransformOp op;
        if(rockets!=null)
        for(Rocket rocket:rockets)
            if (rocket!=null && rocket.getY() > -200) {
                rocketImg = rocketImg(rocket.getKind());
                x = rocketImg.getWidth() / 2;
                y = rocketImg.getHeight() / 2;
                tx = AffineTransform.getRotateInstance(-rocket.getDirection() + Math.toRadians(90), x, y);
                op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                g2d.drawImage(op.filter(rocketImg, null), rocket.getX(), rocket.getY(), this);
            }
    }
    private BufferedImage rocketImg(Kind kind){
        switch (kind){
            case NORMAL:
                return missileImg;
            case UTENSIL:
                return utensilImg;
            case CORN:
                return cornImg;
            case NEUTRON:
                return neutronImg;
        }
        return null;
    }
    private BufferedImage giftImg(Kind kind){
        switch (kind){
            case UTENSIL:
                return utensilGiftImg;
            case CORN:
                return cornGiftImg;
            case NEUTRON:
                return neutronGiftImg;
        }
        return null;
    }
    private void drawObjects(Point[] points,BufferedImage img,Graphics g){
        if(points!=null)
        for(Point point:points )
            g.drawImage(img, point.x, point.y, this);
    }
    private void setPlayer(Map<String,?> graphicInfo){
        if (temp != (Integer) graphicInfo.get("temp")) {
            temp = (Integer) graphicInfo.get("temp");
            tempChange();
        }
        if (maxTemp != (Integer) graphicInfo.get("maxTemp")) {
            maxTemp = (Integer) graphicInfo.get("maxTemp");
            tempBar.setMaximum(maxTemp);
        }
        if (temp == maxTemp)
            tempBar.setString("Cooldown: " + graphicInfo.get("second"));
        bombs = (Integer) graphicInfo.get("bombs");
        coins = (Integer) graphicInfo.get("coins");
        hp = (Integer) graphicInfo.get("hp");
        grade = (Integer) graphicInfo.get("grade");
    }
    private void setVisitor(){
        if(temp!=0){
            temp=0;
            tempChange();
        }
        bombs=0;
        coins=0;
        hp=0;
        grade=0;
    }
    void setPlayers(Map<String, ?> graphicInfo,boolean isPlayer){
        if(graphicInfo==null)
            return;
        planePoints=(Map<String,Point>) graphicInfo.get("planePoints");
            message = (String) graphicInfo.get("message");
        warning = (Boolean) graphicInfo.get("warning");
        rockets = (Rocket[]) graphicInfo.get("rockets");
        bombPoints = (Point[]) graphicInfo.get("bombPositions");
        numberPowerUpPoints = (Point[]) graphicInfo.get("numberPowerUpPoints");
        tempPowerUpPoints = (Point[]) graphicInfo.get("tempPowerUpPoints");
        kindPowerUps = (KindPowerUp[]) graphicInfo.get("kindPowerUps");
        bossTime = (Boolean) graphicInfo.get("bossTime");
        if (bossTime) {
            bossX = (Integer) graphicInfo.get("bossX");
            bossY = (Integer) graphicInfo.get("bossY");
            bossRockets = (Rocket[]) graphicInfo.get("bossRockets");
        }
            else{
            chickensLocations = (Point[]) graphicInfo.get("chickensLocations");
            coinPoints = (Point[]) graphicInfo.get("coinPoints");
            eggPoints = (Point[]) graphicInfo.get("eggPoints");
        }
            boolean playSound=(Boolean) graphicInfo.get("playSound");
            if(playSound)
                SoundPlayer.playSound("./resource/sounds/LongShot.wav",1);
            if(isPlayer)
            setPlayer(graphicInfo);
            else
                setVisitor();
            repaint();
    }
    // must send listeners
    Map<String,?> listenInfo(){
        listenInfo.put("paused",paused);
        listenInfo.put("shouldShoot",shouldShoot);
        listenInfo.put("shootBomb",shootBomb);
        listenInfo.put("mouseX",mouseX);
        listenInfo.put("mouseY",mouseY);
        return listenInfo;
    }

    void informDatabaseConnectionError() {
        JOptionPane.showConfirmDialog(this,"Error connecting to database.","Error",JOptionPane.DEFAULT_OPTION);
    }

    private class MyMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e))
                shouldShoot=true;
            if (SwingUtilities.isRightMouseButton(e))
                shootBomb=true;
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e))
                shouldShoot=false;
            if(SwingUtilities.isRightMouseButton(e))
                shootBomb=false;
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            move(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            move(e);
        }
    }
    private void move(MouseEvent e){
         mouseX=e.getX();
         mouseY=e.getY();
    }

}
