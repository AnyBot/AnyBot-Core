/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.anynet.anybot;

import eu.anynet.anybot.bot.Bot;
import eu.anynet.java.util.CommandLineEvent;
import eu.anynet.java.util.CommandLineListener;
import eu.anynet.java.util.CommandLineParser;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

/**
 *
 * @author sim
 */
public class AnyBot {

   AnyBot()
   {
      CommandLineParser parser = new CommandLineParser();

      parser.addCommandLineListener(new CommandLineListener() {
         @Override
         public void handleCommand(CommandLineEvent e) {
            if(e.get(0).equals("time"))
            {
               Date now = new Date();
               System.out.println("Current time: "+now.toString());
            }
         }
      });

      parser.addCommandLineListener(new CommandLineListener() {
         @Override
         public void handleCommand(CommandLineEvent e) {
            if(e.get(0).equals("say"))
            {
               System.out.println(e.get(1, -1));
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
