package main.game.network;

import main.game.MVC.HoverButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class PlayersPanel extends JPanel {
    static final int SERVER=0,NOT_FULL=1,FULL=2;
    private Box centerBox,southBox;
    private JLabel levelLabel;
    private Font font=new Font("San Fransisco", Font.PLAIN,32);
    private Map<String,JLabel> playersLabels=new HashMap<>();
    PlayersPanel(){
        super();
        setLayout(new BorderLayout());
        setBoxes();
    }
    void add(String name,int grade){
        centerBox.add(Box.createRigidArea(new Dimension(300,100)));
        JLabel label=new JLabel(name+": "+grade);
        centerBox.add(setLabel(label));
        playersLabels.put(name,label);
        revalidate();
        repaint();
    }
    void remove(String name){
        playersLabels.remove(name);
        addAll();
        revalidate();
        repaint();
    }
    private void addAll(){
        centerBox.removeAll();
        centerBox.add(levelLabel);
        for(JLabel label:playersLabels.values()){
            centerBox.add(Box.createRigidArea(new Dimension(300,100)));
            centerBox.add(label);
        }
    }
    void setLevel(int level){
        levelLabel.setText("Level: "+level);
        revalidate();
        repaint();
    }
    private void setBoxes(){
        southBox=Box.createHorizontalBox();
        centerBox =Box.createVerticalBox();
        levelLabel=new JLabel("Level: 0");
        centerBox.add(setLabel(levelLabel));
        add(centerBox,BorderLayout.CENTER);
        add(southBox,BorderLayout.SOUTH);
    }
    private JLabel setLabel(JLabel label){
        label.setForeground(Color.WHITE);
        label.setFont(font);
        return label;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            String defaultPath="./resource/images/background.png";
            BufferedImage backgroundImg = ImageIO.read(new File(defaultPath));
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    void setButtons(int situation){
        switch (situation){
            case SERVER:
                southBox.removeAll();
            setButton("Start",(ActionListener)->{
                NetworkHandler.getInstance().start();
            });
            break;
            case NOT_FULL:
                setVisitButton();
                setButton("Play",(ActionListener)->{
                    NetworkHandler.getInstance().newPlayer();
                });
                break;
            case FULL:
               setVisitButton();
                break;
        }
    }
    private void setVisitButton(){
        southBox.removeAll();
        setButton("Visit",(ActionListener)->{
            NetworkHandler.getInstance().newVisitor();
        });
    }
    private void setButton(String text,ActionListener actionListener){
        JButton button=new JButton(text);
        button.addMouseListener(new HoverButton(button,32));
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFont(font);
        button.setForeground(Color.WHITE);
        button.addActionListener(actionListener);
        southBox.add(button);
        southBox.add(Box.createRigidArea(new Dimension(100,100)));
    }
}
