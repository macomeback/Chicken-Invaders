package main.game.network;

import main.game.MVC.View;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkHandler {
    private static NetworkHandler networkHandler=new NetworkHandler();
    private NetworkObject networkObject;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private NetworkHandler(){

    }
    public static NetworkHandler getInstance(){
        return networkHandler;
    }
    public void newServer(String name,int port,int maxPlayers,int maxLevel){
        networkObject=new Server(name,port,maxPlayers,maxLevel);
    }
    void newPlayer(){
        networkObject=new PlayerClient(View.getView().getCurrentUser(),socket,inputStream,outputStream);
    }
    void newVisitor(){
        networkObject=new VisitorClient(socket,inputStream,outputStream);
    }
    public void newClient(String ip,int port){
        try {
            socket=new Socket(ip,port);
            ObjectInputStream inputStream=new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream=new ObjectOutputStream(socket.getOutputStream());
            this.inputStream=inputStream;
            this.outputStream=outputStream;
            int isFull=inputStream.readInt();
            PlayersPanel panel=(PlayersPanel) inputStream.readObject();
            panel.setButtons(isFull);
            View.getView().showUsers(panel);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    void start(){
        if(networkObject instanceof Server)
            ((Server) networkObject).start();
    }
    public void close(){
            networkObject.close();
    }
}
