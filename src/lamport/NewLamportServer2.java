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
                String[] arr = received.split("#", 5);
                System.out.println(arr[0]);

                if (arr[0].contains("first")) {
                    // obtaining input and out streams 
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Assigning new thread for this client");

                    //server 2
                    // getting localhost ip 
                    InetAddress ip = InetAddress.getByName("localhost");
                    // establish the connection with server port 5054 server 1
                    Socket s1 = new Socket(ip, 5054);
                    // obtaining input and out streams

                    // obtaining input and out streams 
                    DataInputStream dis1 = new DataInputStream(s1.getInputStream());
                    DataOutputStream dos1 = new DataOutputStream(s1.getOutputStream());

                    // server 3
                    Socket s2 = new Socket(ip, 5052);
                    // obtaining input and out streams

                    // obtaining input and out streams 
                    DataInputStream dis2 = new DataInputStream(s2.getInputStream());
                    DataOutputStream dos2 = new DataOutputStream(s2.getOutputStream());

                    long timeStamp = System.currentTimeMillis();
                    // create a new thread object 
                    Thread t = new ClientHandler5(s, dis, dos);

                    Thread sT = new ServerSocketHandler5(s1, dis1, dos1, s2, dis2, dos2, timeStamp, arr[1], dis, dos);

                    // Invoking the start() method 
                    t.start();
                    sT.start();

                    QueueClass5 queueClass = new QueueClass5(timeStamp, 1, arr[1], (ClientHandler5) t, (ServerSocketHandler5) sT);

                    qList.add(queueClass);

                    sortQueue();
                } else if (arr[0].contains("connectedwithserver")) {

                    String[] arrOfStr = arr[1].split(",", 5);
                    System.out.println(arrOfStr[0]);
                    long timeStamp = Long.parseLong(arrOfStr[0]);
                    String message = arrOfStr[1];
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
                    if (qList.size() > 0) {
                        QueueClass5 qClass = qList.get(0);
                        String task = qList.get(0).task;

                        if (qClass.clientClassHandler != null) {

                            qClass.clientClassHandler.dos.writeUTF("acktoclient");
                        }
                        if (qClass.serverSocketHandler != null) {
                            System.out.println("Hello World acks" + qClass.serverSocketHandler.ackFromOthers + "\n");
                            if (qClass.serverSocketHandler.ackFromOthers == 2) {
                                qList.remove(qClass);

                                //1 is entering cS and asking other also
                                qClass.serverSocketHandler.dos1.writeUTF("processin1," + qClass.timestamp + "," + qClass.task);
                                qClass.serverSocketHandler.dos2.writeUTF("processin3," + qClass.timestamp + "," + qClass.task);
                                // do the task here

                                String[] arrOfStr = task.split("@", 5);
                                System.out.println(arrOfStr[0]);
                                if (arrOfStr[0].equals("write")) {
                                    System.out.println("Task detail" + qClass.task + "\n");

                                    try {
                                        String str = arrOfStr[2];

                                        // Open given file in append mode. 
                                        BufferedWriter out = new BufferedWriter(
                                                new FileWriter("/Users/malihasarwat/Documents/Spring2020/AOS/Project/Lamport/src/lamport/" + arrOfStr[1], true));
                                        out.write(str);
                                        out.close();
                                    } catch (IOException e) {
                                        System.out.println("exception occoured" + e);
                                    }
                                }

                                //
                            } else {
                                qClass.serverSocketHandler.dos1.writeUTF("Topackto1," + task);
                                qClass.serverSocketHandler.dos2.writeUTF("Topackto3," + task);
                                System.out.println("Hello World position" + qClass.serverSocketHandler.position + "\n");
                            }

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
        }, 0, 1000);

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
                if (qList.get(i).serverSocketHandler != null) {
                    qList.get(i).serverSocketHandler.position = i;
                }
                if (qList.get(i).clientClassHandler != null) {
                    qList.get(i).clientClassHandler.position = i;
                }

                if (qList.get(i).serverHandler != null) {
                    qList.get(i).serverHandler.position = i;
                }

            }

        }

    }
}

// ClientHandler class 
class ClientHandler5 extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    int position = -1;

    // Constructor 
    public ClientHandler5(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;

        try {
            // Ask user what he wants 
            dos.writeUTF("connectedwithclient");

        } catch (IOException ex) {
            Logger.getLogger(ClientHandler5.class.getName()).log(Level.SEVERE, null, ex);
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
class ServerSocketHandler5 extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis1;
    final DataOutputStream dos1;
    final Socket s1;

    final DataInputStream dis2;
    final DataOutputStream dos2;
    final Socket s2;

    final DataInputStream dis;
    final DataOutputStream dos;

    long timeStamp;
    String receivedMessage;

    public int ackFromOthers = 0;
    int position = -1;
    int processingDone = 0;

    // Constructor 
    public ServerSocketHandler5(Socket s1, DataInputStream dis1, DataOutputStream dos1, Socket s2, DataInputStream dis2, DataOutputStream dos2, long time, String message, DataInputStream dis, DataOutputStream dos) {
        this.s1 = s1;
        this.dis1 = dis1;
        this.dos1 = dos1;
        this.s2 = s2;
        this.dis2 = dis2;
        this.dos2 = dos2;
        this.timeStamp = time;
        this.receivedMessage = message;

        this.dis = dis;
        this.dos = dos;

        try {
            // Ask user what he wants 
            dos1.writeUTF("connectedwithserver#" + timeStamp + "," + receivedMessage);

        } catch (IOException ex) {
            Logger.getLogger(ClientHandler5.class.getName()).log(Level.SEVERE, null, ex);
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
                        //  ackFromOthers += 1;
                        dos1.writeUTF("ok");
                        break;

                    case "ackreceived":
                        dos1.writeUTF("okyoureceivedack");
                        break;
                    //     case "processedinserver2":
                    //         dos1.writeUTF("processedinserver2gotit");
                    //         break;

                    case "ack":

                        dos1.writeUTF("toreturn");
                        break;

                    case "ackto1okfromserver1":

                        dos1.writeUTF("thanksackto1okfromserver1");
                        break;

                    case "Yesackto1okfromserver1":
                        ackFromOthers += 1;
                        dos1.writeUTF("thanksackto1okfromserver1");
                        break;

                    case "processin1done":
                        processingDone += 1;
                        System.out.println("processed count " + processingDone);
                        if (processingDone == 2) {
                            dos.writeUTF("success");
                        }
                        dos1.writeUTF("processin1doneThanks");
                        break;

                    default:
                        dos1.writeUTF("Invalid input");
                        break;
                }

                switch (received2) {

                    case "connected":

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
                        ackFromOthers += 1;
                        dos2.writeUTF("thanksackto2okfromserver3");
                        break;

                    case "processin3done":
                        processingDone += 1;
                        System.out.println("processed count " + processingDone);
                        if (processingDone == 2) {
                            dos.writeUTF("success");
                        }
                        dos2.writeUTF("processin3doneThanks");
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

                    case "okyoureceivedack":
                        toreturn = fortime.format(date);
                        dos.writeUTF(toreturn);
                        break;

                    case "Topackto2":

                        if (this.position == 0) {
                            dos.writeUTF("Yesackto2okfromserver2");
                        } else {
                            dos.writeUTF("Noackto2okfromserver2");
                        }
                        break;

                    case "processin2":
                        for (int i = 0; i < NewLamportServer2.qList.size(); i++) {
                            QueueClass5 q = NewLamportServer2.qList.get(i);
                            System.out.println(NewLamportServer2.qList.get(i).timestamp + ":: " + arrOfStr[1]);
                            System.out.println(NewLamportServer2.qList.get(i).task + ":: " + arrOfStr[2]);
                            if ((NewLamportServer2.qList.get(i).timestamp == Long.parseLong(arrOfStr[1])) && (arrOfStr[2].equals(NewLamportServer2.qList.get(i).task))) {
                                System.out.println("foundHere" + i);
                                //remove it from queue and perform the task now

                                String[] arrOfStr_task = arrOfStr[2].split("@", 5);
                                System.out.println(arrOfStr_task[0]);
                                if (arrOfStr_task[0].equals("write")) {
                                    System.out.println("Task detail" + arrOfStr[2] + "\n");

                                    try {
                                        String str = arrOfStr_task[2];
                                        System.out.println("string to append" + str + "\n");

                                        // Open given file in append mode. 
                                        BufferedWriter out = new BufferedWriter(
                                                new FileWriter("/Users/malihasarwat/Documents/Spring2020/AOS/Project/Lamport/src/lamport/copy" + arrOfStr_task[1], true));
                                        out.write(str);
                                        out.close();
                                    } catch (IOException e) {
                                        System.out.println("exception occoured" + e);
                                    }
                                }
                                NewLamportServer2.qList.remove(i);

                                //
                                dos.writeUTF("processin2done");
                                //
                            }

                        }

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

    public long timestamp;
    int serverId;
    public String task;
    ClientHandler5 clientClassHandler = null;
    ServerSocketHandler5 serverSocketHandler = null;
    ServerHandler5 serverHandler = null;

    // Constructor 
    public QueueClass5(long timeStamp, int serverId, String task, ClientHandler5 c, ServerSocketHandler5 s) {
        this.serverId = serverId;
        this.timestamp = timeStamp;
        this.task = task;
        this.clientClassHandler = c;
        this.serverSocketHandler = s;

    }

    // Constructor 
    public QueueClass5(long timeStamp, int serverId, String task, ServerHandler5 s) {
        this.serverId = serverId;
        this.timestamp = timeStamp;
        this.task = task;
        // this.clientClassHandler = c;
        this.serverHandler = s;

    }

}
