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
          //  startQueueThread();
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
                System.out.println("in main" + received);

                if (received.contains("Client") && !received.contains("socket")) {

                    System.out.println("A new client is connected : " + s);
             //       q.add(received);

             //       System.out.println(q);

                    // obtaining input and out streams 
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Assigning new thread for this client");
                    
                    //server 2
            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost");
            // establish the connection with server port 5058 
            Socket s1 = new Socket(ip, 5058);
            // obtaining input and out streams

            DataInputStream dis1 = new DataInputStream(s1.getInputStream());
            DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream());
            //dos1.writeUTF("socket" + "Server2" + task);
            
            //
            
            //

            //server 3
            // getting localhost ip 
            InetAddress ip2 = InetAddress.getByName("localhost");
            // establish the connection with server port 5059
            Socket s2 = new Socket(ip2, 5059);
            // obtaining input and out streams

            DataInputStream dis2 = new DataInputStream(s2.getInputStream());
            DataOutputStream dos2 = new DataOutputStream(s2.getOutputStream());
            //dos2.writeUTF("socket" + "Server3" + task);

                    /**
                     * ***
                     */
                    Thread t = new ClientHandler(s, dis, dos);
                    t.start();
                    QueueClass queueClass = new QueueClass(System.currentTimeMillis(), 1, received, (ClientHandler) t, null );
                    
                    qList.add(queueClass);
                   // queueClass.setServerSocket();
                    sortQueue();
                    

                    //
                    // Invoking the start() method 
                    
                } else if (received.contains("socket")) {
                    // obtaining input and out streams 
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Assigning new thread for this server");

                    /**
                     * ***
                     */
                    // create a new thread object 
                    Thread serverThread = new ServerHandler(s, dis, dos);
                     serverThread.start();
                    //
                    QueueClass queueClass = new QueueClass(System.currentTimeMillis(), 1, received,  null, (ServerHandler) serverThread );
                    qList.add(queueClass);
                    //
                    sortQueue();

                   
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
                            qClass.clientClassHandler.dos1.writeUTF("ack" + task + "From Server1");
                            qClass.clientClassHandler.dos2.writeUTF("ack" + task + "from Server1");

                            qClass.clientClassHandler.dos.writeUTF("ack" + task + "from server 1Client3");
                        }
                        if (qClass.serverHandler != null) {
                            qClass.serverHandler.dos.writeUTF("ack" + task + "Server1 to other server");
                          //  qClass.serverHandler.dos2.writeUTF("ack" + task + "Server3");

                           
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
    Socket s1;
    Socket s2;
    
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
        setServerSocket("task");
       
    }

    @Override
    public void run() {
        String received;
        String received1;
        String received2;
        String toreturn;
        


        /**
         * *
         */
        try {
                   if (dis1 != null) {
                    received1 = dis1.readUTF();
                    System.out.println(received1);}
                    

                    if (dis2 != null) {
                    //from server 3
                    received2 = dis2.readUTF();
                    System.out.println(received2);}
                    
                    if (dis != null) {
                    received = dis.readUTF();
                    System.out.println(received);}
                    
                    } catch (Exception e) {
            e.printStackTrace();
        }

            
        try {
            //  setServerSocket("this is");
            
            
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
            
            dos.writeUTF("What do you want?[Date | Time]..\n"
                    + "Type Exit to terminate connection.");
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
            while (true) {
                try {

                    // Ask user what he wants 
                    dos.writeUTF("What do you want?[Date | Time]..\n"
                            + "Type Exit to terminate connection.");
                    
                //    dos1.writeUTF("socket What from server 1");
               //     dos2.writeUTF("socket What from server 1");

                    // receive the answer from client 
                   
                    received = dis.readUTF();
                    System.out.println(received); 

                    //from server 2
                   if (dis1 != null) {
                    received1 = dis1.readUTF();
                    System.out.println(received1);
                    
                   
                   }
                    

                    if (dis2 != null) {
                    //from server 3
                    received2 = dis2.readUTF();
                    System.out.println(received2);}
                    

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

    }
    
    public void setSocketRequest (String task){
        try {
            dos1.writeUTF("socket," + "Server2," + task);
            dos1.writeUTF("socket," + "Server2," + task);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
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
    
    // Constructor 
    public QueueClass(long timeStamp, int serverId, String task,ClientHandler c, ServerHandler s) {
        this.serverId = serverId;
        this.timestamp = timeStamp;
        this.task = task;
        this.clientClassHandler = c;
        this.serverHandler = s;
        if(clientClassHandler != null){
          
        }
    }
    

}
