package main.game.network;

import main.game.MVC.View;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

class VisitorClient extends UserClient {
    VisitorClient(Socket socket,ObjectInputStream inputStream,ObjectOutputStream outputStream){
        super(socket,inputStream);
        try {
            outputStream.writeObject("Visitor");
            outputStream.flush();
            waitToStart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            View.getView().setValues((Map<String, ?>) readObject(),false);
        }
        catch (SocketException | EOFException e){
            close();
            View.getView().informConnectionLost();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
