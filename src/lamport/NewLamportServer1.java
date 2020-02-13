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

    public static void main(String[] args) throws IOException {
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5054);

        // running infinite loop for getting 
        // client request 
        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests 
                s = ss.accept();

                System.out.println("A new client is connected : " + s);

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

                // create a new thread object 
                Thread t = new ClientHandler4(s, dis, dos, s1, dis1, dos1);

                // Invoking the start() method 
                t.start();

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }
}

// ClientHandler class 
class ClientHandler4 extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    final DataInputStream dis1;
    final DataOutputStream dos1;
    final Socket s1;

    // Constructor 
    public ClientHandler4(Socket s, DataInputStream dis, DataOutputStream dos, Socket s1, DataInputStream dis1, DataOutputStream dos1) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.s1 = s1;
        this.dis1 = dis1;
        this.dos1 = dos1;

        try {
            // Ask user what he wants 
            dos.writeUTF("What do you want?");

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
                received1 = dis1.readUTF();
                
                System.out.println(received);
                System.out.println(received1);

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

                        dos.writeUTF("hello");
                        break;

                    case "Time":

                        dos.writeUTF("hellhhho");
                        break;

                    case "Nothing":
                        toreturn = fortime.format(date);
                        dos.writeUTF(toreturn);
                        break;

                    default:
                        dos.writeUTF("Invalid input");
                        break;
                }

                switch (received1) {

                    case "Step1":
                        toreturn = fordate.format(date);
                        dos.writeUTF("toreturn");
                        break;

                    case "Step2":
                        toreturn = fortime.format(date);
                        dos.writeUTF("toreturn");
                        break;

                    case "ack":
                        toreturn = fortime.format(date);
                        dos.writeUTF("toreturn");
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
            this.dis1.close();
            this.dos.close();
            this.dos1.close();

        } catch (IOException e) {
            e.printStackTrace();
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
}
