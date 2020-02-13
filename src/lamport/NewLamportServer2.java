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
public class NewLamportServer2  
{ 
    public static void main(String[] args) throws IOException  
    { 
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5051); 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null; 
              
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
                  
                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                  
                System.out.println("Assigning new thread for this client"); 
  
                // create a new thread object 
                Thread t = new ServerHandler5(s, dis, dos); 
  
                // Invoking the start() method 
                t.start(); 
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
  
// ClientHandler class 
class ServerHandler5 extends Thread  
{ 
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
      
  
    // Constructor 
    public ServerHandler5(Socket s, DataInputStream dis, DataOutputStream dos)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        
           try {
            dos.writeUTF("connected");
            
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler4.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
  
    @Override
    public void run()  
    { 
        String received; 
        String toreturn; 
        
        while (true)  
        { 
            try { 
  
                // Ask user what he wants 
              //  dos.writeUTF("server 2"); 
                  
                // receive the answer from client 
                received = dis.readUTF();
                System.out.println(received); 
                  
                if(received.equals("Exit")) 
                {  
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
                  
                    case "ok" : 
                         
                        dos.writeUTF("okfromserver2"); 
                        break; 
                    case "ack" : 
                         
                        dos.writeUTF("okfromserver2"); 
                        break; 
                          
                    case "okyoureceivedack" : 
                        toreturn = fortime.format(date); 
                        dos.writeUTF(toreturn); 
                        break; 
                          
                    default: 
                        //dos.writeUTF("Invalid input"); 
                        break; 
                } 
            } catch (IOException e) { 
                e.printStackTrace(); 
            } 
        } 
          
        try
        { 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
    
    
} 