package lamport;

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

// Server class 
public class LamportServer1 {

 //   public static Queue<String> q = new LinkedList<>();
    public static List<QueueClass> qList = new ArrayList<>();
    public static int flag = 0;

    public static Map<String, ClientHandler> threads = new HashMap<String, ClientHandler>();

    public static void main(String[] args) throws IOException {
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5057);
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

                /**
                 * **
                 */
                String received = new DataInputStream(s.getInputStream()).readUTF();

                if (received.contains("Client")) {

                    System.out.println("A new client is connected : " + s);
             //       q.add(received);

             //       System.out.println(q);

                    // obtaining input and out streams 
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Assigning new thread for this client");

                    /**
                     * ***
                     */
                    // create a new thread object 
                    ClientHandler t = new ClientHandler(s, dis, dos);
                    threads.put(received, (ClientHandler) t);

                    //
                    QueueClass queueClass = new QueueClass();
                    queueClass.task = received;
                    queueClass.serverId = 1;
                    queueClass.timestamp = System.currentTimeMillis();
                    queueClass.clientClassHandler = (ClientHandler) t;
                    qList.add(queueClass);

                    sortQueue();
                    
                   t.qDetail = queueClass;

                    //
                    // Invoking the start() method 
                    t.start();
                } else if (received.contains("Server")) {
                    // obtaining input and out streams 
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Assigning new thread for this server");

                    /**
                     * ***
                     */
                    // create a new thread object 
                    Thread serverThread = new ServerHandler(s, dis, dos);

                    //
                    QueueClass queueClass = new QueueClass();
                    queueClass.task = received;
                    queueClass.serverId = 1;
                    queueClass.timestamp = System.currentTimeMillis();
                    queueClass.serverHandler = (ServerHandler) serverThread;
                    qList.add(queueClass);
                    //
                    sortQueue();

                    serverThread.start();
                }

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
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
                        QueueClass qClass = qList.get(0);
                        String task = qList.get(0).task;

                        if (qClass.clientClassHandler != null) {
                            qClass.clientClassHandler.dos1.writeUTF(task + "Server2");
                            qClass.clientClassHandler.dos2.writeUTF(task + "Server3");

                            qClass.clientClassHandler.dos.writeUTF(task + "Client3");
                        }
                        
                        for(QueueClass qq : qList){
                        System.out.println("Hello World" + qq.timestamp + "\n");
                        }
                    }

                } catch (IOException ex) {
                    Logger.getLogger(LamportServer1.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }, 0, 5000);

    }

    public static void sortQueue() {

        for (int j = 0; j < qList.size(); j++) {

            for (int i = j + 1; i < qList.size(); i++) {

                if (qList.get(i).timestamp < qList.get(j).timestamp) {

                    QueueClass q = qList.get(j);

                    qList.set(j, qList.get(i));

                    qList.set(i, q);

                }

            }

        }

    }
}

// ClientHandler class 
class ClientHandler extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    String name;
    String message;
    
    public QueueClass qDetail;

    // these are for the servers
    DataInputStream dis1;
    DataOutputStream dos1;

    DataInputStream dis2;
    DataOutputStream dos2;

    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String received;
        String toreturn;

        /**
         * *
         */
        try {
            setServerSocket();
              System.out.println(qDetail.task);
              

            // getting localhost ip 
            /*
                InetAddress ip = InetAddress.getByName("localhost"); 
                // establish the connection with server port 5056 
                Socket s1 = new Socket(ip, 5058); 
        // obtaining input and out streams
                
                DataInputStream dis1 = new DataInputStream(s1.getInputStream()); 
                DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream()); 
                dos1.writeUTF("Server"); */
            // create a new thread object 
            //Thread sThread = new ServerHandler(s1, dis1, dos1); 
            // Invoking the start() method 
            //  sThread.start(); 
            /**
             * **
             */
            while (true) {
                try {

                    // Ask user what he wants 
                    dos.writeUTF("What do you want?[Date | Time]..\n"
                            + "Type Exit to terminate connection.");

                    // receive the answer from client 
                    received = dis.readUTF();

                    //from server 2
                    String received1 = dis1.readUTF();
                    System.out.println(received1);

                    //from server 3
                    String received2 = dis2.readUTF();
                    System.out.println(received2);

                    if (received.equals("Exit")) {
                        System.out.println("Client " + this.s + " sends exit...");
                        System.out.println("Closing this connection.");
                        this.s.close();
                        System.out.println("Connection closed");
                        break;
                    }

                    // creating Date object 
                    Date date = new Date();

                    // write on output stream based on the 
                    // answer from the client 
                    switch (received) {

                        case "Date":
                            toreturn = fordate.format(date);
                            dos.writeUTF(toreturn);
                            break;

                        case "Time":
                            toreturn = fortime.format(date);
                            dos.writeUTF(toreturn);
                            break;

                        default:
                            dos.writeUTF("Invalid input");
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // closing resources 
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setServerSocket() {

        try {

            //server 2
            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost");
            // establish the connection with server port 5058 
            Socket s1 = new Socket(ip, 5058);
            // obtaining input and out streams

            dis1 = new DataInputStream(s1.getInputStream());
            dos1 = new DataOutputStream(s1.getOutputStream());
            dos1.writeUTF("Server2" + qDetail.task);
            
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
            dos2.writeUTF("Server3" + qDetail.task);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

// ClientHandler class 
class ServerHandler extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    // Constructor 
    public ServerHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String received;
        String toreturn;
        try {
            dos.writeUTF("from Server 1 to server 2");
            //dos.writeUTF("sending connection request to another server"); 
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {

                // Ask user what he wants 
                //     dos.writeUTF("sending to server everytime inside run"); 
                // receive the answer from client 
                received = dis.readUTF();
                System.out.println(received);
                //dos.writeUTF("sending from 1 to server 2"); 

                if (received.equals("Exit")) {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                // creating Date object 
                Date date = new Date();

                // write on output stream based on the 
                // answer from the client 
                switch (received) {

                    case "Date":
                        toreturn = fordate.format(date);
                        dos.writeUTF(toreturn);
                        break;

                    case "Time":
                        toreturn = fortime.format(date);
                        dos.writeUTF(toreturn);
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

class QueueClass {

    long timestamp;
    int serverId;
    String task;
    ClientHandler clientClassHandler;
    ServerHandler serverHandler;

}
