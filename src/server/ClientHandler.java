package server;

import model.Message;
import model.MessageType;
import model.Streams;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String name;
    private Socket socket;
    private Server server;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (SocketException se) {
            System.out.println("Error establishing connection: " + se.getMessage());
        } catch (IOException ioe) {
            System.out.println("Error establishing connection: " + ioe.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("in Thread");

        try {
            Message message = (Message)in.readObject();
            String str = message.getMessage();
            name = str;


            /*
             * store client's name
             */
            server.getClients().put(name,new Streams(in,out));
            System.out.println(name + " at " + socket.getInetAddress().getHostAddress()+" joined the Chat!");
            server.getsLabel().setText(server.getsLabel().getText() + "\n\n" + name + " at " + socket.getRemoteSocketAddress()+" joined the Chat!");

            /*
             * notify all the clients of new client coming online
             */
            server.getClients().forEach((k,v) -> {
                if(!k.equals(name)) {
                    try {
                        v.getOS().writeObject(new Message(new ArrayList<String>(server.getClients().keySet())));
                        v.getOS().flush();
                    } catch (IOException ioe) {
                        System.out.println("Error establishing connection: " + ioe.getMessage());
                    }
                }
            });

            while(true) {

                message = (Message)in.readObject();
                final Message msg = message;

                if(message.getMessageType() == MessageType.REQUEST_CLIENT_LIST) {

                    out.writeObject(new Message(new ArrayList<String>(server.getClients().keySet())));
                    out.flush();
                } else if(msg.getMessageType() == MessageType.CLIENT_GLOBAL_MESSAGE) {

                    server.getClients().forEach((k,v) -> {
                        try {
                            v.getOS().writeObject(new Message(msg.getMessage(), this.name, MessageType.SERVER_GLOBAL_MESSAGE));
                            v.getOS().flush();
                        } catch (IOException ioe) {
                            System.out.println("Error establishing connection: " + ioe.getMessage());
                        }
                    });
                } else if(message.getMessageType() == MessageType.CLIENT_PRIVATE_MESSAGE) {
                    ObjectOutputStream out_ = server.getClients().get(message.getPerson()).getOS();
                    out_.writeObject(new Message(message.getMessage(), this.name, MessageType.SERVER_PRIVATE_MESSAGE));
                    out_.flush();
                }

            }
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Error establishing connection: " + cnfe.getMessage());
        } catch (SocketException se) {
            System.out.println(name + " at " + socket.getInetAddress().getHostAddress()+" left the Chat!");
            server.getsLabel().setText(server.getsLabel().getText() + "\n\n" + name + " at " + socket.getRemoteSocketAddress()+" left the Chat!");
            server.getClients().remove(name);
            server.getClients().forEach((k,v) -> {
                try {
                    v.getOS().writeObject(new Message(name, MessageType.SEND_CLIENT_LIST_LEFT) );
                    v.getOS().flush();
                } catch (IOException ioe) {
                    System.out.println("Error establishing connection: " + ioe.getMessage());
                }
            });
        } catch (IOException ioe) {
            System.out.println("Error establishing connection: " + ioe.getMessage());
        }
    }
}
