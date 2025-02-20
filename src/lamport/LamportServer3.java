package lamport;

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
  
// Server class 
public class LamportServer3
{ 
    public static void main(String[] args) throws IOException  
    { 
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5059); 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null; 
              
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept(); 
               
                /*****/
                
                String received = new DataInputStream(s.getInputStream()).readUTF(); 
                System.out.println(received); 
                  
                if(received.contains("Client")) {
                    
                System.out.println("A new client is connected : " + s); 
                  
                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                  
                System.out.println("Assigning new thread for this client"); 
                    
                /******/
                
                // create a new thread object 
                Thread t = new ClientHandler3(s, dis, dos); 
  
                // Invoking the start() method 
                t.start(); 
            }
                else if(received.contains("Server")) { 
                    // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                  
                System.out.println("Assigning new thread for this server to server connection"); 
                    
                /******/
                
                // create a new thread object 
                Thread serverThread = new ServerHandler3(s, dis, dos); 
                serverThread.start();
                }
                
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
  
// ClientHandler class 
class ClientHandler3 extends Thread  
{ 
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
    String name;
    String message;
    
    DataInputStream dis1;
    DataOutputStream dos1;
    
    DataInputStream dis2;
    DataOutputStream dos2;
      
  
    // Constructor 
    public ClientHandler3(Socket s, DataInputStream dis, DataOutputStream dos)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
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
                dos.writeUTF("What do you want?[Date | Time]..\n"+ 
                            "Type Exit to terminate connection."); 
                  
                // receive the answer from client 
                received = dis.readUTF(); 
                  
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
                  
                    case "Date" : 
                        toreturn = fordate.format(date); 
                        dos.writeUTF(toreturn); 
                        break; 
                          
                    case "Time" : 
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
          
        try
        { 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    }
    
    
    public void setServerSocket(){
    
          try 
            { 
                
                //server 1
                // getting localhost ip 
                InetAddress ip = InetAddress.getByName("localhost"); 
                // establish the connection with server port 5058 
                Socket s1 = new Socket(ip, 5057); 
                 // obtaining input and out streams
                
                dis1 = new DataInputStream(s1.getInputStream()); 
                dos1 = new DataOutputStream(s1.getOutputStream()); 
                dos1.writeUTF("server3 to Server1 socket"); 
                
                
                //server 2
                
                // getting localhost ip 
                InetAddress ip2 = InetAddress.getByName("localhost"); 
                // establish the connection with server port 5059
                Socket s2 = new Socket(ip2, 5058); 
                // obtaining input and out streams
                
                dis2 = new DataInputStream(s2.getInputStream()); 
                dos2 = new DataOutputStream(s2.getOutputStream()); 
                dos2.writeUTF("server3 to Server2 socket"); 
                
                 }
         catch (Exception e){ 
                e.printStackTrace(); 
            } 
    
    }
}
    
    
    // ClientHandler class 
class ServerHandler3 extends Thread  
{ 
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
      
  
    // Constructor 
    public ServerHandler3(Socket s, DataInputStream dis, DataOutputStream dos)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
    } 
  
    @Override
    public void run()  
    { 
        String received; 
        String toreturn; 
        try { 
        dos.writeUTF("what update from you server 1 to me server 3"); 
         } catch (IOException e) { 
                e.printStackTrace(); 
            }
        
        while (true)  
        { 
            try { 
  
                // Ask user what he wants 
              //  dos.writeUTF("what update from you server 1 to me server 2"); 
                  
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
                  
                    case "Write" : 
                        toreturn = fordate.format(date); 
                        dos.writeUTF(toreturn); 
                        break; 
                          
                    case "Read" : 
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
 
