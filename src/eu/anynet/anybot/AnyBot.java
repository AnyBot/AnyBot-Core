/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot;

import eu.anynet.anybot.bot.Bot;
import eu.anynet.anybot.bot.NamedThreadPool;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

/**
 *
 * @author sim
 */
public class AnyBot {


   class MyTestThread extends Thread
   {

      @Override
      public void run() {
         int count =0;
         while(!Thread.currentThread().isInterrupted())
         {
            count++;
            System.out.println("["+this.getName()+"] Hello, "+count+". run, interrupted: "+(this.isInterrupted()?"true":"false"));
            try {
               Thread.sleep(3000);
            } catch (InterruptedException ex) {
               System.out.println("Interrupted!");
            }
         }
         System.out.println("["+this.getName()+"] Bye!");
      }

   }


   AnyBot()
   {
      CommandLineParser parser = new CommandLineParser();

      final NamedThreadPool pool = new NamedThreadPool();

      parser.addCommandLineListener(new CommandLineListener() {
         @Override
         public void handleCommand(CommandLineEvent e) {
            if(e.get(0).equals("start") && e.count()>1)
            {
               MyTestThread newthread = new MyTestThread();
               newthread.setName(e.get(1));
               pool.add(newthread);
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener() {

         @Override
         public void handleCommand(CommandLineEvent e) {
            if(e.get(0).equals("stop") && e.count()>1)
            {
               pool.cancel(e.get(1));
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener() {
         @Override
         public void handleCommand(CommandLineEvent e) {
            if(e.get(0).equals("exit"))
            {
               System.exit(0);
            }
         }
      });

      System.out.println("Welcome to a demo shell!");
      Scanner in = new Scanner(System.in);
      while(true)
      {
         System.out.print("> ");
         parser.handleCommandLine(in.nextLine());
      }

   }


   /**
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {
      /*Bot bot = new Bot();
      try {
      bot.connect("iz-smart.net");
      } catch (NickAlreadyInUseException ex) {
      Logger.getLogger(AnyBot.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IrcException | IOException ex) {
      Logger.getLogger(AnyBot.class.getName()).log(Level.SEVERE, null, ex);
      }*/
      AnyBot anyBot = new AnyBot();

   }

}
