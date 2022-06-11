package main.game.MVC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class View {
    private static View view;
    private JPanel mainPanel;
    private GamePanel gamePanel;
    private UIPanel uiPanel;
    private RankPanel rankPanel;
    private MultiPlayerPanel multiPlayerPanel;
    private JPanel playersPanel;
    private CardLayout cardLayout;
    private String currentUser;
    private SingleHandler singleHandler=new SingleHandler();
    private boolean multiPlayer=false;
    private View(){
        beginGraphics();
        initiateFrame();
    }
    public static View getView(){
        if(view==null)
            view=new View();
        return view;
    }
    void setCurrentUser(String currentUser){
        this.currentUser=currentUser;
    }
    public String getCurrentUser(){
        return currentUser;
    }
    private void beginGraphics(){
        cardLayout=new CardLayout();
        mainPanel=new JPanel(cardLayout);
        uiPanel = new UIPanel();
        rankPanel=new RankPanel();
        multiPlayerPanel=new MultiPlayerPanel();
        gamePanel=new GamePanel();
        Logic.setWidthHeight();
        mainPanel.add(uiPanel, "UI");
        mainPanel.add(rankPanel,"Rank");
        mainPanel.add(multiPlayerPanel,"Multi");
        mainPanel.add(gamePanel,"Game");
        uiPanel.addFirstPage();
        uiPanel.loadUsers(InfoHandler.getInfoHandler().userNames());
        cardLayout.show(mainPanel, "UI");
    }
    public void showUsers(JPanel panel){
        if(playersPanel!=null)
            mainPanel.remove(playersPanel);
        playersPanel=panel;
        mainPanel.add(panel,"Players");
        cardLayout.show(mainPanel,"Players");
    }
    private void singlePlayerGame(boolean isNew){
        singleHandler.startGame(isNew);
        showGame();
    }
    void informDatabaseConnectionError(){
        gamePanel.informDatabaseConnectionError();
    }
    private void showGame(){
        cardLayout.show(mainPanel,"Game");
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
    }
    public void show(State state){
        switch (state){
            case NEWGAME:
               singlePlayerGame(true);
                break;
            case MULTIPLAYER:
                multiPlayer=true;
                cardLayout.show(mainPanel,"Multi");
                break;
            case MULTIGAME:
                showGame();
                break;
            case CONTINUE:
               singlePlayerGame(false);
                break;
            case UI:
                exitGame();
                Logic.clear();
                uiPanel.addFirstPage();
                cardLayout.show(mainPanel,"UI");
                break;
            case RANK:
               exitGame();
                rankPanel.initialize();
                cardLayout.show(mainPanel,"Rank");
                break;
        }
    }
    private void exitGame(){
        if(!multiPlayer)
            InfoHandler.getInfoHandler().addUserInfo(singleHandler.getUserMap());
    }
    public void informConnectionLost(){
        JOptionPane.showConfirmDialog(null,"Connection Lost","",JOptionPane.DEFAULT_OPTION);
        View.getView().show(State.UI);
    }
    public int pauseMessage(){
      return gamePanel.pauseMessage();
    }
    public Map listenInfo(){
        return gamePanel.listenInfo();
    }
    public void setValues(Map<String,?> graphicInfo,boolean isPlayer){
        gamePanel.setPlayers(graphicInfo,isPlayer);
    }
    private void initiateFrame(){
        JFrame frame=new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(1200,1000);
        frame.add(mainPanel);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Map<String,?> userMap=null;
                if(singleHandler!=null)
                    userMap=singleHandler.getUserMap();
                    if(userMap!=null)
                    InfoHandler.getInfoHandler().addUserInfo(userMap);
                    InfoHandler.getInfoHandler().save();
            }
        });
    }
}
