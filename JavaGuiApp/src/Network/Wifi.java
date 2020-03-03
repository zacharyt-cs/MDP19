package Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Wifi {
    private Socket clientSocket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private String address;
    private int port;

    public Wifi(String address, int port){
        this.address = address;
        this.port = port;

    }

    public void connect(){
        try{
            clientSocket = new Socket(address, port);
        } catch (Exception e){
            //Todo
        }
    }

    public void disconnect(){
        try{
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (Exception e){
            //Todo
        }
    }

    public void sendMsg(String msg){


    }

    public String receiveMsg(){
        return null;
    }
}
