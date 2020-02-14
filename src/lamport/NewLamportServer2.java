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
public class NewLamportServer2 {

    public static List<QueueClass5> qList = new ArrayList<>();
    public static int flag = 0;

    public static void main(String[] args) throws IOException {
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5051);
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

                if (received.contains("connectedwithserver")) {

                    String[] arrOfStr = received.split(",", 5);
                    System.out.println(arrOfStr[0]);
                    long timeStamp = Long.parseLong(arrOfStr[1]);
                    String message = arrOfStr[2];
                    // obtaining input and out streams 
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Assigning new thread for this client");

                    // create a new thread object 
                    Thread t = new ServerHandler5(s, dis, dos, timeStamp, message);
                    QueueClass5 queueClass = new QueueClass5(timeStamp, 1, message, (ServerHandler5) t);

                    qList.add(queueClass);

                    dos.writeUTF("connected");

                    // Invoking the start() method 
                    t.start();
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
                        QueueClass5 qClass = qList.get(0);
                        String task = qList.get(0).task;
                        System.out.println("Hello World task in server 2" + task + "\n");
                        if (qClass.clientClassHandler != null) {

                            qClass.clientClassHandler.dos.writeUTF("acktoclient");
                        }
                        if (qClass.serverHandler != null) {
                           // qClass.serverHandler.dos.writeUTF("processedinserver2");
                            // qClass.serverHandler.dos2.writeUTF("ackto3," + task);

                        }

                        for (QueueClass5 qq : qList) {
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

                    QueueClass5 q = qList.get(j);

                    qList.set(j, qList.get(i));

                    qList.set(i, q);

                }

            }

            for (int i = 0; i < qList.size(); i++) {
                QueueClass5 q = qList.get(i);
                /* if (qList.get(i).serverHandler != null) {
                    qList.get(i).serverHandler.position = i;
                }
                if (qList.get(i).clientClassHandler != null) {
                    qList.get(i).clientClassHandler.position = i;
                }*/

                if (qList.get(i).serverHandler != null) {
                    qList.get(i).serverHandler.position = i;
                }

            }

        }

    }
}

// ClientHandler class 
class ServerHandler5 extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    long timeStamp;
    String receivedMessage;
    int position = -1;

    // Constructor 
    public ServerHandler5(Socket s, DataInputStream dis, DataOutputStream dos, long time, String message) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.timeStamp = time;
        this.receivedMessage = message;

        /* try {
            dos.writeUTF("connected");

        } catch (IOException ex) {
            Logger.getLogger(ClientHandler4.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    @Override
    public void run() {
        String received;
        String toreturn;

        while (true) {
            try {

                // Ask user what he wants 
                //  dos.writeUTF("server 2"); 
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

                // creating Date object 
                Date date = new Date();

                // write on output stream based on the 
                // answer from the client 
                String[] arrOfStr = received.split(",", 5);
                System.out.println(arrOfStr[0]);
                switch (arrOfStr[0]) {

                    case "ok":

                        dos.writeUTF("okfromserver2");
                        break;
                    case "ackto2":

                        dos.writeUTF("ackto2okfromserver2");
                        break;
                    case "connectedwithserver": // not working

                        dos.writeUTF("connectedwithserver2Received");
                        break;
                    case "okyoureceivedack":
                        toreturn = fortime.format(date);
                        dos.writeUTF(toreturn);
                        break;

                    case "Topackto2":

                        if(this.position ==0)
                        dos.writeUTF("Yesackto2okfromserver2");
                        else dos.writeUTF("Noackto2okfromserver2");
                        break;
                    default:
                        //dos.writeUTF("Invalid input"); 
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

class QueueClass5 {

    long timestamp;
    int serverId;
    String task;
    ClientHandler4 clientClassHandler;
    ServerHandler5 serverHandler;

    // Constructor 
    public QueueClass5(long timeStamp, int serverId, String task, ServerHandler5 s) {
        this.serverId = serverId;
        this.timestamp = timeStamp;
        this.task = task;
        // this.clientClassHandler = c;
        this.serverHandler = s;

    }

}
