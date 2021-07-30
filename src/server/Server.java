package server;

import model.Streams;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class Server {

    private ServerSocket server;
    private JFrame frame;
    private JTextArea sLabel;
    private JScrollPane sLabelPane;
    private int pNumber;
    private Hashtable<String,Streams> clients = new Hashtable<String,Streams>();


    public Server() {
        clients = new Hashtable<String, Streams>();
        String message = "Using port number 50000\nTo listen to clients through a different port, type the port number:\n";
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String str = (String)JOptionPane.showInputDialog(frame, message, "50000");
                if(str == null) System.exit(0);
                str = str.trim();
                pNumber = Integer.parseInt(str);
                try {
                    Server.this.server = new ServerSocket(pNumber);
                } catch(IOException ioe) {
                    System.out.println("Cannot connect to server: " + ioe.getMessage());
                }
                if (server==null) {
                    JOptionPane.showMessageDialog(frame, "Port in Use!", "Error!" , JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                setGUI();
            }
        });
    }

    public void setGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame();
        try {
            sLabel = new JTextArea();
            sLabelPane = new JScrollPane();
            sLabelPane.setViewportView(sLabel);
            sLabel.setEditable(false);
            sLabel.setText("Server Listening at: \n" + InetAddress.getLocalHost() + "\nPort: " + pNumber + "\n\nIf client is on the same machine,\nuse localhost as the IP address\nand port as mentioned above,\nelse if on a different machine,\nuse the above IP address and port number.");
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
        frame.add(sLabelPane);
        frame.setPreferredSize(new Dimension(255,200));
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startListening();
    }

    private void startListening() {

        new SwingWorker<Object,Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                while(true) {
                    try {
                        Socket socket = server.accept();
                        new Thread(new ClientHandler(Server.this, socket)).start();
                        System.out.println("Client connected at: " + socket.getRemoteSocketAddress());
                    } catch(IOException ioe) {
                        System.out.println("Error establishing connection: " + ioe.getMessage());
                    }

                }
            }
        }.execute();
    }

    public Hashtable<String,Streams> getClients() {
        return clients;
    }

    public JTextArea getsLabel() {
        return sLabel;
    }
}
