package main.game.network;
import main.game.MVC.FeatureAdder;
import main.game.MVC.View;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
class PlayerClient extends UserClient{
    private ObjectOutputStream outputStream;
    PlayerClient(String name, Socket socket, ObjectInputStream inputStream,ObjectOutputStream outputStream){
        super(socket,inputStream);
        try {
            this.outputStream=outputStream;
            outputStream.flush();
            writeObject("Player");
            writeObject(name);
            waitToStart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void writeObject(Object object) throws IOException {
        outputStream.writeObject(object);
        outputStream.reset();
        outputStream.flush();
    }
            @Override
            public void run() {
            try {
                Object input = readObject();
                if("Pause".equals(input)){
                    int x=View.getView().pauseMessage();
                    if(x==0) {
                        close();
                        return;
                    }
                    else if(x==2){
                        writeObject("Add attribute");
                        writeObject(FeatureAdder.getInstance().getClassFile());
                        writeObject(FeatureAdder.getInstance().getFullClassName());
                        return;
                    }
                }
                 if(input instanceof Map)
                        View.getView().setValues((Map<String, ?>) input,true);
                 writeObject(View.getView().listenInfo());
            }
            catch (SocketException e){
                close();
                View.getView().informConnectionLost();
            }
            catch (Throwable e){
                e.printStackTrace();
            }
//            catch (IOException e) {
//            e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
        }

    }


