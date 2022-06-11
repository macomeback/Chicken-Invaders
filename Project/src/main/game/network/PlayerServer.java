package main.game.network;

import main.game.MVC.FeatureAdder;
import main.game.MVC.Logic;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

class PlayerServer extends UserServer{
    private ObjectInputStream inputStream;
    private Logic logic;
    private String name;
    PlayerServer(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream, int maxLevel) throws IOException {
        super(socket,outputStream);
        try {
            this.inputStream=inputStream;
            name=(String)inputStream.readObject();
            logic=Logic.newLogic(name,maxLevel);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
        String getName(){
        return name;
        }
        int getGrade(){
        return logic.getGrade();
        }
    @Override
    void close(){
        super.close();
        Logic.remove(logic);
    }
        @Override
        public void run() {
        try {
            setIO();
        }
        catch (SocketException e){
            close();
        }
        catch (Throwable e){
            e.printStackTrace();
        }
//        catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        }
        private void checkInput() throws IOException, ClassNotFoundException {
        Object input=inputStream.readObject();
        if(input instanceof Map)
            logic.read((Map<String,?>)input);
        else if("Add attribute".equals(input)){
            File file=(File)inputStream.readObject();
            String path=(String) inputStream.readObject();
            FeatureAdder.getInstance().loadClass(file,path);
        }
        }
        private void setIO() throws IOException, ClassNotFoundException {
        writeObject(logic.graphicInfo());
            logic.updateTime();
            checkInput();
        }
}
