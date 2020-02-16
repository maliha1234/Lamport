/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author malihasarwat
 */
public class TestFileWrite {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try { 
  
            // Open given file in append mode. 
            BufferedWriter out = new BufferedWriter( 
                   new FileWriter("/Users/malihasarwat/Documents/Spring2020/AOS/Project/Lamport/src/lamport/file1.txt", true)); 
            out.write("str"); 
            out.close(); 
        } 
        catch (IOException e) { 
            System.out.println("exception occoured" + e); 
        } 
    }

}
