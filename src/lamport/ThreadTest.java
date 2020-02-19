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

class MyThread extends Thread {
    //Initially setting the flag as true

    private volatile boolean flag = true;

    //This method will set flag as false
    public void stopRunning() {
        flag = false;
    }

    @Override
    public void run() {
        //Keep the task in while loop

        //This will make thread continue to run until flag becomes false
        while (flag) {
            System.out.println("I am running....");
        }

        System.out.println("Stopped Running....");
        System.out.println("Stopped Running....");
    }
}

public class ThreadTest {

    public static void main(String[] args) {
        MyThread thread = new MyThread();

        thread.start();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //call stopRunning() method whenever you want to stop a thread
        thread.stopRunning();

     //   System.out.println("In main again again again....");
        
         System.out.println("In main again again....");
         System.out.println("In main again again....");
         System.out.println("In main again again....");
         System.out.println("In main again again....");
         
         thread = new MyThread();

        thread.start();
        
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //call stopRunning() method whenever you want to stop a thread
        thread.stopRunning();

     //   System.out.println("In main again again again....");
        
         System.out.println("In main again again2....");
         System.out.println("In main again again2....");
         System.out.println("In main again again2....");
         System.out.println("In main again again2....");
    }
}
