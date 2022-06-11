package main.game.MVC;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class UIPanel extends JPanel {
//    /Users/homaun/IdeaProjects/Project
    private String defaultPath="./resource/";
    private JButton addUser=new JButton("Add user"),removeUser=new JButton("Remove user" ),login=new JButton("Login"),rankBoard=new JButton("Hall of fame");
    private ArrayList<JRadioButton> userButtons;
    private String currentUser="";
    private ButtonGroup buttonGroup =new ButtonGroup();
    private Box radioBox=Box.createVerticalBox();
    private Clip clip;
    private JSlider soundControl;
    UIPanel(){
        super();
    setLayout(new BorderLayout());
    userButtons =new ArrayList<>();
        initializeButtons();
        clip=SoundPlayer.playSound("./resource/sounds/UpbeatFunk.wav");
    }
    void loadUsers(Set<String> users){
        for(String user: users) setAddUser(user,false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage backgroundImg;
        try {
            backgroundImg = ImageIO.read(new File(defaultPath+"images/background.png"));
            g.drawImage(backgroundImg,0,0,getWidth(),getHeight(),null);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private boolean contains(String st){
        for(JRadioButton jrb: userButtons)
            if(st.equals(jrb.getActionCommand()))
                return true;
        return false;
    }
    private void setCenter(){
        JLabel ln,lw;
        ln=new JLabel();
        lw=new JLabel();
        ln.setPreferredSize(new Dimension(1200,200));
        lw.setPreferredSize(new Dimension(600,200));
        add(ln,BorderLayout.NORTH);
        add(lw,BorderLayout.WEST);
    }
    private void setRadioButton(JRadioButton jrb){
        jrb.setForeground(Color.GREEN);
        jrb.setFont(new Font("San Fransisco",Font.ITALIC,30));
        radioBox.add(jrb);
        buttonGroup.add(jrb);
    }
    private void setButton(JButton[] b,Box box,boolean horver,int size){
        if(box==null) {
            box=Box.createHorizontalBox();
            add(box,BorderLayout.SOUTH);
            box.add(Box.createRigidArea(new Dimension(250,0)));
        }
        Font font=new Font("San Fransisco", Font.BOLD, size);
        for(int i=0;i<b.length;i++) {
            b[i].setForeground(Color.WHITE);
            b[i].setFont(font);
            b[i].setBorderPainted(false);
            b[i].setOpaque(false);
            b[i].setContentAreaFilled(false);
            box.add(b[i]);
            if (horver) box.add(Box.createRigidArea(new Dimension(50, 0)));
            else box.add(Box.createRigidArea(new Dimension(0, 50)));
            b[i].addMouseListener(new HoverButton(b[i],size));
        }
    }
    private void initializeButtons(){
        addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!setAddUser(JOptionPane.showInputDialog("Enter username"),true))
                    JOptionPane.showMessageDialog(UIPanel.this,"The username already exists");
            }
        });
        removeUser.addActionListener((ActionListener) -> {
                setRemoveUser();
        });
        login.addActionListener((ActionListener) ->{
                if(currentUser.equals("")){
                    JOptionPane.showMessageDialog(UIPanel.this,"No user selected");
                    return;
                }
                InfoHandler.getInfoHandler().setLogger(currentUser);
                removeAll();
                repaint();
                revalidate();
                nextPage();
        });
        rankBoard.addActionListener((ActionListener) ->{
                View.getView().show(State.RANK);
        });
        JButton[] b={addUser,removeUser,login,rankBoard};
       setButton(b,null,true,20);
    }
    private void nextPage(){
        JButton continu,newGame,exit;
        continu=new JButton("Continue");
        newGame=new JButton("New game");
        exit=new JButton("Exit");
        // adding actionlisteners to buttons before entering the game
        continu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                View.getView().show(State.CONTINUE);
            }
        });
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                addFirstPage();
            }
        });
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               multiSingle();
            }
        });
        // end of actionlisteners
        JButton[] b={continu,newGame,exit};
        setSecondaryButtons(b,false,30);
       initializeSoundSlider();
    }
    private void multiSingle(){
        removeAll();
        JButton singlePlayer,multiPlayer;
        multiPlayer=new JButton("MultiPlayer");
        singlePlayer=new JButton("SinglePlayer");
        singlePlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                View.getView().show(State.NEWGAME);
                removeAll();
            }
        });
        multiPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               View.getView().show(State.MULTIPLAYER);
               removeAll();
                }
        });
        JButton[] buttons={singlePlayer,multiPlayer};
        setSecondaryButtons(buttons,true,30);
        revalidate();
        repaint();
    }
    private void setSecondaryButtons(JButton[] buttons,boolean horver,int size){
        setCenter();
        Box box=Box.createVerticalBox();
        add(box,BorderLayout.CENTER);
        setButton(buttons,box,false,30);
    }
    private void initializeSoundSlider(){
         soundControl=new JSlider(-600,60,0);
        soundControl.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                FloatControl gainControl=(FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
               float volume=soundControl.getValue()/10;
                gainControl.setValue(volume);
            }
        });
        handleSliderLayout();
           }
    private void handleSliderLayout(){
        Box box=Box.createHorizontalBox();
        add(box,BorderLayout.NORTH);
        JLabel volume=new JLabel("Volume");
        volume.setFont(new Font("San Fransisco",Font.BOLD,20));
        volume.setForeground(Color.BLUE);
        box.add(Box.createRigidArea(new Dimension(400,100)));
        box.add(volume);
        box.add(soundControl);
        box.add(Box.createRigidArea(new Dimension(400,100)));
    }
    void addFirstPage(){
        setCenter();
        add(radioBox,BorderLayout.CENTER);
        JButton[] jb={addUser,removeUser,login,rankBoard};
        setButton(jb,null,true,20);
        revalidate();
        repaint();
    }
private boolean setAddUser(String name,boolean isNew){
 if(!contains(name) || !name.equals("")) {
     JRadioButton jrb=new JRadioButton(name);
     jrb.setActionCommand(name);
     jrb.addActionListener(e->
       {
           currentUser=e.getActionCommand();
             View.getView().setCurrentUser(currentUser);
         }
     );
     if(isNew)
     InfoHandler.getInfoHandler().addUserInfo(name);
     userButtons.add(jrb);
     setRadioButton(jrb);
     revalidate();
     repaint();
 return true;
 }
 return false;
}
private void setRemoveUser(){
    if (currentUser.equals("")) {
        JOptionPane.showMessageDialog(this,"No user selected");
        return;
    }
    InfoHandler.getInfoHandler().removeUserInfo(currentUser);
  removeButton();
}
private void removeButton(){
    radioBox.remove(getCurrentUserButton());
    buttonGroup.remove(getCurrentUserButton());
    userButtons.remove(currentUser);
    revalidate();
    repaint();
}
private JRadioButton getCurrentUserButton(){
        for(JRadioButton jrb: userButtons) if(jrb.getActionCommand().equals(currentUser)) return jrb;
        return null;
    }
}
