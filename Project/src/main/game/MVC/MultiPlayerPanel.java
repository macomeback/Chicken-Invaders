package main.game.MVC;

import main.game.network.NetworkHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class MultiPlayerPanel extends JPanel {
    MultiPlayerPanel(){
        super();
        setLayout(new BorderLayout());
        initializeMultiPlayerButtons();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            String defaultPath="./resource/";
            BufferedImage backgroundImg = ImageIO.read(new File(defaultPath + "images/background.png"));
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void initializeMultiPlayerButtons(){
        JButton server,client;
        server=new JButton("Server");
        client=new JButton("Client");
        server.addActionListener((ActionListener) ->{
                JTextField portText=new JTextField("Port"),maxPlayers=new JTextField("Max Players"),maxLevel=new JTextField("Max Level");
                if(getNetworkInfo(portText,maxPlayers,maxLevel)== JOptionPane.OK_OPTION)
                    NetworkHandler.getInstance().newServer(View.getView().getCurrentUser(), Integer.parseInt(portText.getText()), Integer.parseInt(maxPlayers.getText()), Integer.parseInt(maxLevel.getText()));
        });
        client.addActionListener((ActionListener)->{
            JTextField ip=new JTextField("IP"),port=new JTextField("Port");
            if(getNetworkInfo(ip,port)==JOptionPane.OK_OPTION)
                NetworkHandler.getInstance().newClient(ip.getText(),Integer.parseInt(port.getText()));
        });
        JButton[] buttons={server,client};
        setButtons(buttons);
    }
    private void setButtons(JButton... buttons){
        Box box=Box.createVerticalBox();
        add(BorderLayout.CENTER,box);
        Font font=new Font("San Fransisco",Font.PLAIN,30);
        for(JButton button:buttons){
            box.add(Box.createRigidArea(new Dimension(100,100)));
            button.addMouseListener(new HoverButton(button,32));
            button.setFont(font);
            button.setForeground(Color.WHITE);
            button.setOpaque(false);
            button.setBorderPainted(false);
            box.add(button);
        }
    }
    private int getNetworkInfo(JTextField... textFields){
        JPanel panel=new JPanel();
        for(JTextField textField:textFields)
        {
            panel.add(textField);
            panel.add(Box.createHorizontalStrut(15));
        }
        return JOptionPane.showConfirmDialog(null,panel,"",JOptionPane.OK_CANCEL_OPTION);
    }
}
