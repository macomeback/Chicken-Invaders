package main.game.network;
import main.game.MVC.View;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

abstract class UserServer extends NetworkObject {
    private Socket socket;
    private ObjectOutputStream outputStream;
    UserServer(Socket socket,ObjectOutputStream outputStream) throws IOException {
        this.socket=socket;
        this.outputStream=outputStream;
        outputStream.flush();
    }
     void writeObject(Object object){
        try {
            outputStream.writeObject(object);
            outputStream.flush();
            outputStream.reset();
        }
        catch (SocketException e){
            close();
            View.getView().informConnectionLost();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    void close(){
        super.close();
        try {
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    void start(){
        writeObject("ShowGame");
        scheduleTask();
    }
}
