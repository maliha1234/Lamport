/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamport;

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Server class 
public class NewLamportServer1 {

    public static List<QueueClass4> qList = new ArrayList<>();
    public static int flag = 0;
    public static Map<String, QueueClass4> threads = new HashMap<String, QueueClass4>();

    public static void main(String[] args) throws IOException {
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5054);

        if (flag == 0) {
            startQueueThread();
            flag = 1;
        }
        // running infinite loop for getting 
        // client request 
        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests 
                s = ss.accept();

                System.out.println("A new client is connected : " + s);
                String received = new DataInputStream(s.getInputStream()).readUTF();
                System.out.println("in main" + received);

                if (received.contains("client") && !received.contains("socket")) {
                    // obtaining input and out streams 
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Assigning new thread for this client");

                    //server 2
                    // getting localhost ip 
                    InetAddress ip = InetAddress.getByName("localhost");
                    // establish the connection with server port 5058 
                    Socket s1 = new Socket(ip, 5051);
                    // obtaining input and out streams

                    // obtaining input and out streams 
                    DataInputStream dis1 = new DataInputStream(s1.getInputStream());
                    DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream());

                    Socket s2 = new Socket(ip, 5052);
                    // obtaining input and out streams

                    // obtaining input and out streams 
                    DataInputStream dis2 = new DataInputStream(s2.getInputStream());
                    DataOutputStream dos2 = new DataOutputStream(s2.getOutputStream());

                    long timeStamp = System.currentTimeMillis();
                    // create a new thread object 
                    Thread t = new ClientHandler4(s, dis, dos);

                    Thread sT = new ServerSocketHandler4(s1, dis1, dos1, s2, dis2, dos2, timeStamp, received);

                    // Invoking the start() method 
                    t.start();
                    sT.start();

                    QueueClass4 queueClass = new QueueClass4(timeStamp, 1, received, (ClientHandler4) t, (ServerSocketHandler4) sT);

                    qList.add(queueClass);

                    sortQueue();
                }

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }

    public static void sortQueue() {

        for (int j = 0; j < qList.size(); j++) {

            for (int i = j + 1; i < qList.size(); i++) {

                if (qList.get(i).timestamp < qList.get(j).timestamp) {

                    QueueClass4 q = qList.get(j);

                    qList.set(j, qList.get(i));

                    qList.set(i, q);

                }

            }

            for (int i = 0; i < qList.size(); i++) {
                QueueClass4 q = qList.get(i);
                if (qList.get(i).serverSocketHandler != null) {
                    qList.get(i).serverSocketHandler.position = i;
                }
                if (qList.get(i).clientClassHandler != null) {
                    qList.get(i).clientClassHandler.position = i;
                }
            }

        }

    }

    public static void startQueueThread() throws IOException {
        Timer t = new Timer();
        System.out.println("Hello World Timer ini");
        t.schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    System.out.println("Hello World" + qList);
                    if (!qList.isEmpty()) {
                        QueueClass4 qClass = qList.get(0);
                        String task = qList.get(0).task;
                        System.out.println("Hello World acks" + qClass.serverSocketHandler.ackFromOthers + "\n");
                        if (qClass.clientClassHandler != null) {

                            qClass.clientClassHandler.dos.writeUTF("acktoclient");
                        }
                        if (qClass.serverSocketHandler != null) {
                            qClass.serverSocketHandler.dos1.writeUTF("Topackto2," + task);
                            qClass.serverSocketHandler.dos2.writeUTF("Topackto3," + task);
                            System.out.println("Hello World position" + qClass.serverSocketHandler.position + "\n");

                        }

                        for (QueueClass4 qq : qList) {
                            System.out.println("Hello World" + qq.timestamp + "\n");
                        }
                    }

                } catch (IOException ex) {
                    Logger.getLogger(LamportServer1.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }, 0, 5000);

    }
}

// ClientHandler class 
class ClientHandler4 extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    int position = -1;

    // Constructor 
    public ClientHandler4(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;

        try {
            // Ask user what he wants 
            dos.writeUTF("connectedwithclient");

        } catch (IOException ex) {
            Logger.getLogger(ClientHandler4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        String received;
        String received1;
        String toreturn;

        while (true) {
            try {

                // receive the answer from client 
                received = dis.readUTF();

                System.out.println(received);

                if (received.equals("Exit")) {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                // write on output stream based on the 
                // answer from the client 
                switch (received) {

                    case "ackReceived":

                        dos.writeUTF("thanks");
                        break;
                    case "HYk":

                        dos.writeUTF("thanks");
                        break;

                    default:
                        dos.writeUTF("Invalid input");
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // closing resources 
            this.dis.close();

            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// ClientHandler class 
class ServerSocketHandler4 extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis1;
    final DataOutputStream dos1;
    final Socket s1;

    final DataInputStream dis2;
    final DataOutputStream dos2;
    final Socket s2;
    long timeStamp;
    String receivedMessage;

    public int ackFromOthers = 0;
    int position = -1;

    // Constructor 
    public ServerSocketHandler4(Socket s1, DataInputStream dis1, DataOutputStream dos1, Socket s2, DataInputStream dis2, DataOutputStream dos2, long time, String message) {
        this.s1 = s1;
        this.dis1 = dis1;
        this.dos1 = dos1;
        this.s2 = s2;
        this.dis2 = dis2;
        this.dos2 = dos2;
        this.timeStamp = time;
        this.receivedMessage = message;

        try {
            // Ask user what he wants 
            dos1.writeUTF("connectedwithserver," + timeStamp + "," + receivedMessage);

        } catch (IOException ex) {
            Logger.getLogger(ClientHandler4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        String received1;
        String received2;

        while (true) {
            try {

                received1 = dis1.readUTF();
                received2 = dis2.readUTF();
                System.out.println(received1);
                System.out.println(received2);

                switch (received1) {

                    case "connected":
                        ackFromOthers += 1;
                        dos1.writeUTF("ok");
                        break;

                    case "ackreceived":
                        dos1.writeUTF("okyoureceivedack");
                        break;
                    case "processedinserver2":
                        dos1.writeUTF("processedinserver2gotit");
                        break;

                    case "ack":

                        dos1.writeUTF("toreturn");
                        break;

                    case "ackto2okfromserver2":

                        dos1.writeUTF("thanksackto2okfromserver2");
                        break;

                    case "Yesackto2okfromserver2":

                        dos1.writeUTF("thanksackto2okfromserver2");
                        break;

                    default:
                        dos1.writeUTF("Invalid input");
                        break;
                }

                switch (received2) {

                    case "connected":
                        ackFromOthers += 1;
                        dos2.writeUTF("ok");
                        break;

                    case "ackreceived":
                        dos2.writeUTF("okyoureceivedack");
                        break;

                    case "ack":

                        dos2.writeUTF("toreturn");
                        break;

                    case "ackto3okfromserver3":

                        dos2.writeUTF("thanksackto3okfromserver3");
                        break;
                    case "Yesackto2okfromserver3":

                        dos1.writeUTF("thanksackto2okfromserver3");
                        break;

                    default:
                        dos2.writeUTF("Invalid input");
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

/* 
    public void setServerSocket(String task) {

        try {

            //server 2
            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost");
            // establish the connection with server port 5058 
            Socket s1 = new Socket(ip, 5058);
            // obtaining input and out streams

            dis1 = new DataInputStream(s1.getInputStream());
            dos1 = new DataOutputStream(s1.getOutputStream());
            dos1.writeUTF("socket" + "Server2" + task);
            
            //
            
            //

            //server 3
            // getting localhost ip 
            InetAddress ip2 = InetAddress.getByName("localhost");
            // establish the connection with server port 5059
            Socket s2 = new Socket(ip2, 5059);
            // obtaining input and out streams

            dis2 = new DataInputStream(s2.getInputStream());
            dos2 = new DataOutputStream(s2.getOutputStream());
            dos2.writeUTF("socket" + "Server3" + task);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
class QueueClass4 {

    long timestamp;
    int serverId;
    String task;
    ClientHandler4 clientClassHandler = null;
    ServerSocketHandler4 serverSocketHandler = null;

    // Constructor 
    public QueueClass4(long timeStamp, int serverId, String task, ClientHandler4 c, ServerSocketHandler4 s) {
        this.serverId = serverId;
        this.timestamp = timeStamp;
        this.task = task;
        this.clientClassHandler = c;
        this.serverSocketHandler = s;

    }
}
