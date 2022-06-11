package main.game.network;
import main.game.MVC.State;
import main.game.MVC.View;
import javax.swing.JPanel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
abstract class UserClient extends NetworkObject{
    private Socket socket;
    private ObjectInputStream inputStream;
    UserClient(Socket socket, ObjectInputStream inputStream){
        this.socket=socket;
        this.inputStream=inputStream;
    }
    void waitToStart(){
        Thread thread=new Thread(() ->{
            try {
                Object input = readObject();
                while (input instanceof JPanel) {
                    View.getView().showUsers((JPanel) input);
                    input = readObject();
                }
                if ("ShowGame".equals(input)) {
                    View.getView().show(State.MULTIGAME);
                    scheduleTask();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
    Object readObject() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }
    @Override
    void close(){
        super.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
