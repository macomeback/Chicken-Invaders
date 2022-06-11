package main.game.network;
import main.game.MVC.FeatureAdder;
import main.game.MVC.Logic;
import main.game.MVC.State;
import main.game.MVC.View;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
class Server extends NetworkObject{
    private int maxLevel, maxPlayers;
    private ServerSocket serverSocket;
    private String name;
    private int level,wave;
    private Logic logic;
    private boolean isPaused=false,isStarted=false;
    private List<PlayerServer> players = new ArrayList<>();
    private List<PlayerServer> waitingPlayers=new ArrayList<>();
    private List<VisitorServer> visitors = new ArrayList<>();
    private List<PlayerServer> previousPlayers=new ArrayList<>();
    private List<VisitorServer> previousVisitors=new ArrayList<>();
    private PlayersPanel playersPanel=new PlayersPanel();
    private boolean finished;
    Server(String name, int port, int maxPlayers, int maxLevel){
        this.name = name;
        this.maxLevel = maxLevel;
        this.maxPlayers = maxPlayers;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        listen();
    }
    private void listen() {
        Thread thread = new Thread((() -> {
                try {
                    logic = Logic.newLogic(name, maxLevel);
                    playersPanel.add(name,logic.getGrade());
                    showPlayersPanel();
                    while (!serverSocket.isClosed())
                        {
                            Socket socket = serverSocket.accept();
                            playOrVisit(socket);
                        }
                }
                catch (SocketException e){

                }
                catch (IOException e) {
                        e.printStackTrace();
                }
        }));
        thread.start();
    }
    private void playOrVisit(Socket socket){
        Thread thread=new Thread(()->{
            try {
                ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream.writeInt(isFull());
                outputStream.flush();
                outputStream.writeObject(playersPanel);
                outputStream.flush();
                String kind = (String) inputStream.readObject();
                if (kind.equals("Visitor"))
                    addVisitor(socket,outputStream);
                else if (kind.equals("Player"))
                        addPlayer(socket,inputStream,outputStream);
                showPlayersPanel();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
    private void addVisitor(Socket socket,ObjectOutputStream outputStream) throws IOException {
        VisitorServer visitor=new VisitorServer(socket, outputStream);
        visitors.add(visitor);
        if(isStarted)
            visitor.start();
    }
    private void addPlayer(Socket socket,ObjectInputStream inputStream,ObjectOutputStream outputStream) throws IOException {
        PlayerServer player = new PlayerServer(socket,inputStream,outputStream,maxLevel);
        if(isStarted)
            waitingPlayers.add(player);
        else
            addPlayer(player);
    }
    private void addPlayer(PlayerServer player){
        players.add(player);
        playersPanel.add(player.getName(),player.getGrade());
    }
    private int isFull(){
       if(players.size()+waitingPlayers.size()<maxPlayers-1)
           return PlayersPanel.NOT_FULL;
       return PlayersPanel.FULL;
    }
    private void showPlayersPanel(){
        playersPanel.setButtons(isFull());
        for(PlayerServer waitingPlayer:waitingPlayers)
            waitingPlayer.writeObject(playersPanel);
        if(!isStarted) {
            for (PlayerServer player : players)
                player.writeObject(playersPanel);
            for (VisitorServer visitor : visitors)
                visitor.writeObject(playersPanel);
            View.getView().showUsers(playersPanel);
        }
        playersPanel.setButtons(PlayersPanel.SERVER);
        playersPanel.revalidate();
        playersPanel.repaint();
    }
    void start() {
        isStarted=true;
        scheduleTask();
        for (PlayerServer player : players)
            player.start();
        for(VisitorServer visitor: visitors)
            visitor.start();
        View.getView().show(State.MULTIGAME);
    }
    @Override
    void close(){
        super.close();
        try {
            serverSocket.close();
            closeAll();
            View.getView().show(State.UI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void closeAll(){
        for (PlayerServer player : players)
            player.close();
        for(PlayerServer waitingPlayer: waitingPlayers)
            waitingPlayer.close();
        for(VisitorServer visitor:visitors)
            visitor.close();
    }
    @Override
    public void run() {
        try {
            if (finished) {
                System.out.println("Finished");
                close();
                return;
            }
            setWave();
            checkPaused();
            checkOpenUsers();
            setIO();
        }catch (Throwable e){
            e.printStackTrace();
        }
    }
    private void nextWave(){
        wave++;
        for(PlayerServer player:waitingPlayers) {
            addPlayer(player);
            player.start();
        }
        waitingPlayers.clear();
        showPlayersPanel();
    }
    private void setWave(){
        if(logic.getWave()!=wave) {
           nextWave();
            if(wave%4+1!=level) {
                level++;
                playersPanel.setLevel(level);
            }
        }
    }
    private void checkPaused(){
        if(Logic.isPaused() && !isPaused) {
            for (PlayerServer player : players)
                player.writeObject("Pause");
            checkPauseMessage();
        }
        isPaused=Logic.isPaused();
    }
    private void checkPauseMessage(){
        int x=View.getView().pauseMessage();
        if(x==0)
            finished=true;
        else if(x==2)
            FeatureAdder.getInstance().loadClass();
    }
    private void checkOpenUsers(){
        for(Iterator<PlayerServer> i=players.iterator(); i.hasNext();){
            PlayerServer player=i.next();
            if(player.isClosed()){
                previousPlayers.add(player);
                playersPanel.remove(player.getName());
            }
        }
        players.removeAll(previousPlayers);
        previousPlayers.clear();
        for(Iterator<VisitorServer> j=visitors.iterator();j.hasNext();){
            VisitorServer visitor=j.next();
            if(visitor.isClosed())
                previousVisitors.add(visitor);
        }
        visitors.removeAll(previousVisitors);
    }
    private void setIO() {
        Map input=View.getView().listenInfo();
        logic.read(input);
        Logic.staticUpdateTime();
        logic.updateTime();
        Map<String,?> graphicInfo=logic.graphicInfo();
        VisitorServer.setGraphicInfo(graphicInfo);
        View.getView().setValues(graphicInfo,true);
    }
}

