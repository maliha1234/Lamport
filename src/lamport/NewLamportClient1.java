/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamport;

import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client class 
public class NewLamportClient1 {

    public static void main(String[] args) throws IOException {
        try {
            Scanner scn = new Scanner(System.in);

            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 5056 
            Socket s = new Socket(ip, 5054);

            // obtaining input and out streams 
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF("write@file1.txt@aa@client");

            // the following loop performs the exchange of 
            // information between client and client handler 
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
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }

            }

            // closing resources 
            scn.close();
            dis.close();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
