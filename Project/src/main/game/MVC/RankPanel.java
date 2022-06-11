package main.game.MVC;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;
import java.util.Map;
class RankPanel extends JPanel {
    private Map<String,?> users;
    private List<String> userNames;
    private Font nameFont=new Font("San Fransisco",Font.BOLD,30),font=new Font("San Fransisco",Font.PLAIN,30);
    void initialize(){
        setLayout(new BorderLayout());
        users= InfoHandler.getInfoHandler().getUsers();
        userNames= Arrays.asList(users.keySet().toArray(new String[users.size()]));
        sort();
        JButton back=new JButton("Back");
        back.addActionListener((ActionListener)-> {
           View.getView().show(State.UI);
        });
        setButton(back);
        setGraphics();
    }
    private void setGraphics(){
        setTable();
        revalidate();
        repaint();
    }
    private void setButton(JButton button){
            button.setFont(font);
            button.setForeground(Color.WHITE);
            button.setOpaque(false);
            button.setBorderPainted(false);
            Box northBox=Box.createHorizontalBox();
            northBox.add(Box.createRigidArea(new Dimension(1000,200)));
            add(northBox,BorderLayout.NORTH);
            Box box=Box.createHorizontalBox();
            box.add(Box.createRigidArea(new Dimension(300,100)));
            box.add(button);
            add(box,BorderLayout.SOUTH);
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        try {
            String defaultPath="./resource/";
            BufferedImage backgroundImg = ImageIO.read(new File(defaultPath + "background.png"));
            g.drawImage(backgroundImg,0,0,getWidth(),getHeight(),null);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setTable(){
        Box box=Box.createVerticalBox();
        for(int i=0;i<userNames.size();i++)
            box.add(user(userNames.get(i)));
        box.add(Box.createRigidArea(new Dimension(300,100)));
        add(box,BorderLayout.CENTER);
    }
    private Box user(String name){
        Box box=Box.createHorizontalBox();
        box.add(setLabel(name+": ",nameFont));
            Map<String, ?> user =(Map<String,?>)users.get(name);
            box.add(setLabel("Wave: " + user.get("wave") + " ", font));
            box.add(setLabel("Grade: " + user.get("grade") + " ", font));
            box.add(setLabel("Time: " + user.get("secondsPlayed"), font));
        return box;
    }
    private JLabel setLabel(String string,Font font){
        JLabel label=new JLabel();
        label.setFont(font);
        label.setForeground(Color.WHITE);
        label.setText(string);
        return label;
    }
    private void sort(){
        for(int i=0;i<userNames.size();i++)
            for(int j=i+1;j<userNames.size();j++)
                if(!compareTo((Map<String,?>)users.get(userNames.get(i)),(Map<String,?>)users.get(userNames.get(j))))
                    Collections.swap(userNames,i,j);
    }
 private boolean compareTo(Map<String,?> first,Map<String,?> second){
     int firs,secon;
     firs=(Integer) first.get("wave");
     secon=(Integer) second.get("wave");
     if(firs!=secon)
         return firs>secon;
     firs=(Integer) first.get("grade");
     secon=(Integer) second.get("grade");
     if(firs!=secon)
         return firs>secon;
     long fir,sec;
     fir=(Long) first.get("time");
     sec=(Long) second.get("time");
     if(fir!=sec)
         return fir<sec;
     return true;
 }
}
