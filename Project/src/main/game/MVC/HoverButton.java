package main.game.MVC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoverButton extends MouseAdapter {
    JButton button;
    Font fontEntered,fontExited;
    public HoverButton(JButton b,int size){
        this.button =b;
        fontEntered=new Font("San Fransisco",Font.ITALIC,size);
        fontExited=new Font("San Fransisco",Font.PLAIN,size);
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        button.setFont(fontEntered);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        button.setFont(fontExited);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        button.setBorderPainted(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        button.setBorderPainted(false);
    }
}
