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

// Client class 
public class NewLamportClient2 {

    public static void main(String[] args) throws IOException {
        try {

            for (int i = 0; i < 1; i++) {

                // getting localhost ip 
                InetAddress ip = InetAddress.getByName("localhost");

                // establish the connection with server port 5056 
                Socket s = new Socket(ip, 5054);

                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                MyThread1 thread1 = new MyThread1(s, dis, dos);

                thread1.start();

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //call stopRunning() method whenever you want to stop a thread
                thread1.stopRunning();

                if (thread1.result.equals("success")) {
                    System.out.println("This is success....");
                } else {
                    System.out.println("This is fail....");
                }

                // the following loop performs the exchange of 
                // information between client and client handler 
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MyThread1 extends Thread {
    //Initially setting the flag as true

    private volatile boolean flag = true;
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    String result;

    public MyThread1(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        try {
            dos.writeUTF("write@file1.txt@bb@client");
        } catch (IOException ex) {
            Logger.getLogger(MyThread1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //This method will set flag as false
    public void stopRunning() {

        flag = false;

    }

    @Override
    public void run() {

        try {
            //Keep the task in while loop
            //This will make thread continue to run until flag becomes false
            while (flag) {
                // System.out.println("I am running....");

                try {
                    while (true) {

                        // printing date or time as requested by client
                        String received = dis.readUTF();
                        System.out.println(received);

                        switch (received) {

                            case "connectedwithclient":

                                dos.writeUTF("HYk");
                                break;

                            case "acktoclient":
                                dos.writeUTF("acktoclientreceived");
                                break;

                            case "success":
                                result = "success";
                                // dos.writeUTF("closing");

                                break;

                            default:
                                //  dos.writeUTF("Invalid input");
                                break;
                        }
                        // String tosend = scn.nextLine();
                        // dos.writeUTF("tosend");

                        // If client sends exit,close this connection
                        // and then break from the while loop
                        if ("tosend".equals("Exit")) {
                            System.out.println("Closing this connection : " + s);

                            System.out.println("Connection closed");
                            break;
                        }

                    }
                    // closing resources

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            dos.writeUTF("closing");
            s.close();
            dis.close();
            dos.close();
            System.out.println("Stopped Running....");
            System.out.println("Stopped Running....");
        } catch (IOException ex) {
            Logger.getLogger(MyThread1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
