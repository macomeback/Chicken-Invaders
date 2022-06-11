package main.game.network;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
class VisitorServer extends UserServer {
    private static Map<String,?> graphicInfo;
   VisitorServer(Socket socket, ObjectOutputStream outputStream) throws IOException {
        super(socket,outputStream);
    }
    static void setGraphicInfo(Map<String,?> graphicInfo){
        VisitorServer.graphicInfo=graphicInfo;
    }
    @Override
    public void run() {
           writeObject(graphicInfo);
    }
}
